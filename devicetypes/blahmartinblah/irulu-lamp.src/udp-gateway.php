<?php
////////////////////////////////////////////////////////////////////////////////////////////////////
// Based on http://www.tinkerfailure.com/lightwaverf-php-script/
// PHP code for interfacing with the WiFi-Link from LightwaveRF.
// Written by Tinker Failure 2012. http://www.tinkerfailure.com
// Based on code by Steven, which can be found here: http://blog.networkedsolutions.co.uk/?p=149
//////////////////////////////////////////////////////////////////////////////////////////////////////
// This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
// You may use and update this code as you see fit. But you must attribute it's source.
// Full details here: http://creativecommons.org/licenses/by-nc-sa/3.0/
////////////////////////////////////////////////////////////////////////////////////////////////////

function singleBroadcast($broadcastip_tosend, $udbport_tosend, $message_tosend)
    {
    $sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
    socket_set_option($sock, SOL_SOCKET, SO_BROADCAST, 1);
    socket_sendto($sock, $message_tosend, strlen($message_tosend), 0, '192.168.1.255', $udbport_tosend);
    socket_close($sock);
    }

function multiBroadcast($id_to_send, $status_to_send)
    {
    $broadcastid = $id_to_send;
    $broadcaststatus = $status_to_send;
    $broadcast_string = $code . ",!" . $broadcastid .$broadcaststatus . "|";
    $port = 9760;
    $sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
    socket_set_option($sock, SOL_SOCKET, SO_BROADCAST, 1);
    socket_sendto($sock, $broadcast_string, strlen($broadcast_string), 0, '255.255.255.255', $port);
    socket_close($sock);
    }

function statecontrol($state_to_convert)
    {
        if ($state_to_convert == "on")
        {
            $devicestatus = "F1";
        }
        else if ($state_to_convert == "off")
        {
            $devicestatus = "F0";
        }
        else if ($state_to_convert == "offALL")
        {
            $devicestatus = "Fa";
        }
        else if ($state_to_convert == "mood01")
        {
            $devicestatus = "FmP1";
        }
        else if ($state_to_convert == "mood02")
        {
            $devicestatus = "FmP2";
        }                
        else if ($state_to_convert == "mood03")
        {
            $devicestatus = "FmP3";
        }
        else if ($state_to_convert == "1")
        {
            $devicestatus = "FdP1";
        }
        else if ($state_to_convert >= "2")
        {
            $devicestatus = "FdP" . ROUND($state * 0.32);
        }
    return $devicestatus;
    }

$code = "000";
$udbport = $_GET["udbport"];
$broadcastip = $_GET["broadcastip"];
$message = $_GET["message"];
$state = $_GET["state"];

if (!isset($udbport)) die("No UDB port specified (udbport=)");
if (!isset($broadcastip)) die("No Broadcast IP set (broadcastip=)");

singleBroadcast($broadcastip, $udbport, $message);

/*
echo "<p>$id</p>";
echo "<p>$node</p>";
echo "<p>$status</p>";
*/
?>
