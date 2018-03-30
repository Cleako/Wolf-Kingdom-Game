<?php
define('LUNA_ROOT', '../');
require LUNA_ROOT.'include/common.php';

require_once('paypal_settings.php');

/*

PayPal IPN with PHP
How To Implement an Instant Payment Notification listener script in PHP
http://www.micahcarrick.com/paypal-ipn-with-php.html

COMPATIBLE WITH LATEST PHP 5 AND APACHE, AND READY TO PLUGIN FOR FLUXBB. AUTHOR DAVVE.

(c) 2011 - Micah Carrick & DAVVE

*/

// tell PHP to log errors to ipn_errors.log in this directory
ini_set('log_errors', true);
ini_set('error_log', dirname(__FILE__).'/ipn_errors.log');

// intantiate the IPN listener
include('ipnlistener.php');
$listener = new IpnListener();

// tell the IPN listener to use the PayPal test sandbox
$listener->use_sandbox = false;

// try to process the IPN POST
try {
    $listener->requirePostMethod();
    $verified = $listener->processIpn();
} catch (Exception $e) {
    error_log($e->getMessage());
    exit(0);
}

if ($verified) {

    $errmsg = '';   // stores errors from fraud checks
    
    // 1. Make sure the payment status is "Completed" 
    if ($_POST['payment_status'] != 'Completed') { 
        // simply ignore any IPN that is not completed
        exit(0); 
    }

    // 2. Make sure seller email matches your primary account email.
    if ($_POST['receiver_email'] != $settings['sellerEmail']) {
        $errmsg .= "'receiver_email' does not match: ";
        $errmsg .= $_POST['receiver_email']."\n";
    }
    
    // 3. Make sure the amount(s) paid match
    /*if ($_POST['mc_gross'] != '9.99') {
        $errmsg .= "'mc_gross' does not match: ";
        $errmsg .= $_POST['mc_gross']."\n";
    }*/
    
    // 4. Make sure the currency code matches
    if ($_POST['mc_currency'] != 'USD') {
        $errmsg .= "'mc_currency' does not match: ";
        $errmsg .= $_POST['mc_currency']."\n";
    }

    $txn_id = $db->escape($_POST['txn_id']);
    $sql = "SELECT COUNT(*) FROM " . GAME_BASE . "orders WHERE txn_id = '$txn_id'";
    $r = $db->query($sql);
    if (!$r) {
        error_log($db->error());
        exit(0);
    }
    
    $exists = $db->result($r, 0);
	$db->free_result($r);
    
    if ($exists) {
        $errmsg .= "'txn_id' has already been processed: ".$_POST['txn_id']."\n";
    }
    
    if (!empty($errmsg)) {
    
        // manually investigate errors from the fraud checking
        $body = "IPN failed fraud checks: \n$errmsg\n\n";
        $body .= $listener->getTextReport();
		 mail($settings['sellerEmail'], 'IPN Fraud Warning', $body, "From: ".$settings['sellerEmail']."");
    
    } else {
        // add this order to a table of completed orders
        $payer_email = $db->escape($_POST['payer_email']);
		$mc_gross = $db->escape($_POST['mc_gross']);
		
		foreach($donationOffers as $key => $value) {
			if($value['Price'] == $mc_gross) {
				$jewels = $value['Jewel'];
				$stones = $value['Stone'];
			} 
		}
		
		$custom = $db->escape($_POST['custom']);
		$db->query("INSERT INTO `" . GAME_BASE . "orders`(`txn_id`, `payer_email`, `paid`, `jewels_purchased`, `user`, `time`) VALUES ('$txn_id', '$payer_email', '$mc_gross', '$jewels', '$custom', '".time()."')");
		$db->query("UPDATE `users` SET jewels=jewels + '$jewels', teleport_stone=teleport_stone + '$stones' WHERE id='$custom'");
		new_notification($custom, 'donate.php', __('You have completed your donation ('.$mc_gross.'$ / '.$jewels.' jewels)!', 'luna'), 'fa fa-paypal');
					
		if (!$db->query($sql)) {
            error_log($db->error());
            exit(0);
        }
    }
    
} else {
    // manually investigate the invalid IPN
    mail($settings['sellerEmail'], 'Invalid IPN', $listener->getTextReport() , "From: ".$settings['sellerEmail']."");
}

?>
