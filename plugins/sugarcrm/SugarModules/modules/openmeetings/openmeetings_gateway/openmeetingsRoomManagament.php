<?php
/*********************************************************************************
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License") +  you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*  ********************************************************************************/

if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point');

require_once("openmeetings_gateway.php");

global $current_user;
global $system_config;


class openmeetingsRoomManagament {


	function displayAdminError($errorString){
		$output = '<p class="error">' . $errorString .'</p>';
			return $output;
	}
	//Deprecated 
	function getRoomHash(&$focus, $event) {
		
		global $current_user;
		$becomemoderator = 1;	
	
		$openmeetings_gateway = new openmeetings_gateway();
		
	
		if ($openmeetings_gateway->openmeetings_loginuser()) {
		
				$roomid = $openmeetings_gateway->openmeetings_createroomwithmod($test);
		
		 		$roomhash = $openmeetings_gateway->openmeetings_setUserObjectAndGenerateRoomHash($current_user->user_name,$current_user->first_name,
						$current_user->last_name,$current_user->picture,$current_user->email1,$current_user->id,"SugarCRM" , $focus->openmeetings_roomid_c, 1, 1);
				echo   "room id: $roomid";
				echo "room hash:  $roomhash ";

		}
	}
	
	//Deprecated please use	addUpdateRoomWithModerationExternalTypeAndTopBarOption instead.
	function createRoomWithMod(&$focus, $event) {
		global $sugar_config;
		
		
		//if ($focus->openmeetings_roomname_c == "" && $focus->is_openmeetings_c == true){

			$openmeetings_gateway = new openmeetings_gateway();
			if ($openmeetings_gateway->openmeetings_loginuser()) {

				$openmeetings->name = "SUGARCRM_MEETINGROOM_".$focus->id;
				$openmeetings->roomtypes_id = 1;
				$openmeetings->comment = 'Created by SOAP-Gateway for SUGARCM Platform';
				$openmeetings->numberOfPartizipants = 100;
				$openmeetings->ispublic = false;
				$openmeetings->appointment = false;
				$openmeetings->isDemoRoom = false;
				$openmeetings->demoTime = 0;
				$openmeetings->isModeratedRoom = true;
		
				$roomid = $openmeetings_gateway->openmeetings_createroomwithmod($openmeetings);	
				$focus->room_id = $roomid;
				
				//$focus->openmeetings_roomname_c = ""; 		
				//$focus->openmeetings_roomname_c = $sugar_config[site_url]."/index.php?module=Openmeetings&action=room_entrance&roomid=".$focus->openmeetings_roomid_c;
				
				$focus->room_link = $sugar_config[site_url]."/index.php?module=openmeetings&action=room_entrance&roomid=".$focus->room_id;
						
			} else {
				echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration";
				//$this->displayAdminError("Errorr");
				/*
				echo "<pre>";
				print_r($focus);							
				echo "</pre>";	
				exit();
				*/
				exit();
				//$focus->room_link = "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration";
				//$focus->room_link = $this->displayAdminError(translate('LBL_ERORR_LOGIN', 'openmeetings'));
				
			}
		//}
	}
	
	
	function addUpdateRoomWithModerationExternalTypeAndTopBarOption(&$focus, $event) {		
		global $sugar_config;		
		if(isset($focus->room_id)){
			
			$openmeetings_gateway = new openmeetings_gateway();
			if ($openmeetings_gateway->openmeetings_loginuser()) {
				
				$openmeetings = '';
							
					
				$openmeetings->room_id = $focus->room_id;										
				$openmeetings->name = $focus->name;
				$openmeetings->roomtypes_id = $focus->room_type_id;
				$openmeetings->comment = $focus->description;
				$openmeetings->numberOfPartizipants = $focus->number_of_partizipants;
				$openmeetings->ispublic = $focus->is_public;
				$openmeetings->appointment = 0;
				$openmeetings->isDemoRoom = 0;
				$openmeetings->demoTime = "";
				$openmeetings->isModeratedRoom = $focus->is_moderated_room;						
				$openmeetings->allowUserQuestions = $focus->allow_user_questions;
				
				//future options not implemented in openmeetings_gateway
				$openmeetings->isAudioOnly = $focus->is_audio_only;
				$openmeetings->allowRecording = $focus->allow_recording;				
				$openmeetings->waitForRecording = false;
				$openmeetings->hideTopBar = true;
				$openmeetings->externalRoomType = 'SugarCRM';				
			
				$roomid = $openmeetings_gateway->updateRoomWithModerationAndQuestions($openmeetings);	
				
				//$focus->room_link = $sugar_config[site_url]."/index.php?module=openmeetings&action=room_entrance&roomid=".$focus->room_id;
					
				return $roomid;
			} else {
				die("Could not login User to OpenMeetings, check your OpenMeetings Module Configuration");					
			}
			
		}else{
		
			$openmeetings_gateway = new openmeetings_gateway();
			if ($openmeetings_gateway->openmeetings_loginuser()) {
				//global $sugar_config;
				$openmeetings = '';
										
				$openmeetings->name = $focus->name;
				$openmeetings->roomtypes_id = $focus->room_type_id;
				$openmeetings->comment = $focus->description;
				$openmeetings->numberOfPartizipants = $focus->number_of_partizipants;
				$openmeetings->ispublic = $focus->is_public;
				$openmeetings->appointment = 0;
				$openmeetings->isDemoRoom = 0;
				$openmeetings->demoTime = "";
				$openmeetings->isModeratedRoom = $focus->is_moderated_room;
						
				$openmeetings->allowUserQuestions = $focus->allow_user_questions;
				$openmeetings->isAudioOnly = $focus->is_audio_only;
				$openmeetings->allowRecording = $focus->allow_recording;	
				
				//future options
				$openmeetings->waitForRecording = false;
				$openmeetings->hideTopBar = true;
				$openmeetings->externalRoomType = 'SugarCRM';				
			
				$room_id = $openmeetings_gateway->openmeetings_addRoomWithModerationExternalTypeAndTopBarOption($openmeetings);	
				
				$focus->room_id = $room_id;
				$focus->room_link = $sugar_config[site_url]."/index.php?module=openmeetings&action=room_entrance&roomid=".$room_id;
					
				return $room_id;
			
							
			} else {
				die("Could not login User to OpenMeetings, check your OpenMeetings Module Configuration");			
			}
		}
	}

/*
	function createMyRoomWithMod(&$focus, $event) {
		global $sugar_config;
				
		
		
		if ($sugar_config->openmeetings_username == "" & $focus->openmeetings_my_roomid_c == ""){

			$openmeetings_gateway = new openmeetings_gateway();
			if ($openmeetings_gateway->openmeetings_loginuser()) {
		
				$roomid = $openmeetings_gateway->openmeetings_createroomwithmod($test);	
				//$focus->openmeetings_roomid_c = $roomid;
				
				//$focus->openmeetings_roomname_c = ""; 		
				//$focus->openmeetings_my_room_c = $sugar_config[site_url]."/index.php?module=Openmeetings&action=room_entrance&roomid=".$roomid;
				
				$focus->openmeetings_my_roomid_c = $roomid;
				$focus->save(false);
						
			} else {
				echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration";
				exit();
			}
		}
	}

*/

	function createMyRoomWithMod() {
		global $sugar_config;
		global $current_user;

		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {

		/*	'name' => 'SUGARCM_ROOM',
			'roomtypes_id' => 1,
			'comment' => 'Created by SOAP-Gateway for SUGARCM Platform',
			'numberOfPartizipants' => 100,
			'ispublic' => false,
			'appointment' => false, 
			'isDemoRoom' => false, 
			'demoTime' => 0, 
			'isModeratedRoom' => $isModeratedRoom
		*/

			$openmeetings->name = "SUGARCRM_MY_ROOM_".$current_user->id;
			$openmeetings->roomtypes_id = 1;
			$openmeetings->comment = 'Created by SOAP-Gateway for SUGARCM Platform';
			$openmeetings->numberOfPartizipants = 100;
			$openmeetings->ispublic = false;
			$openmeetings->appointment = false;
			$openmeetings->isDemoRoom = false;
			$openmeetings->demoTime = 0;
			$openmeetings->isModeratedRoom = true;
		
			$roomid = $openmeetings_gateway->openmeetings_createroomwithmod($openmeetings);	
				
			return $roomid;
						
		} else {
			die("Could not login User to OpenMeetings, check your OpenMeetings Module Configuration");	
		}
	}


	function getInvitationHash(&$focus, $event) {
		
		
		//echo "<pre>";
		//print_r($_POST);
		//echo $_REQUEST['send_invites'];
	//print_r($focus);
		//print_r($focus->conf_op0b58nigs_ida);
		//print_r($focus->openmeetings_meetings->beans[$focus->conf_op0b58nigs_ida]->room_link);	
		//exit;
			
		//echo "</pre>";

		global $sugar_config;
		global $system_config;
		global $timedate;
		global $current_user;
		
				
		$dateStart = $focus->date_start;
		$timeFromat = $timedate->get_date_time_format();
		$toTimeFormat = $timedate->get_db_date_time_format();		
		
		
		$date_start_in_db_fmt = DateTime::createFromFormat($toTimeFormat, $dateStart);
		$date_start_in_db_fmt_str= $date_start_in_db_fmt->format($toTimeFormat);
		
		$startDateString = $date_start_in_db_fmt->format('d.m.Y'); //dd.mm.yyyy
		$startTimeString = $date_start_in_db_fmt->format('H:i');
		
		
		$date_start_timestamp= strtotime($date_start_in_db_fmt_str);
		$date_end_timestamp = $date_start_timestamp + (( $focus->duration_hours * 3600 )+ ($focus->duration_minutes * 60));

		$endDateString = date("d.m.Y", $date_end_timestamp); //dd.mm.yyyy
		$endTimeString = date("H:i", $date_end_timestamp);
		
/*
echo '<pre>'; 

echo "startDateString ".$startDateString;
echo "\n";

echo "startTimeString ".$startTimeString;
echo "\n";
echo "endDateString ". $endDateString;
echo "\n";
echo "endTimeString ". $endTimeString;
echo "\n";
exit();

echo '</pre>';
*/
		//!empty($focus->conf_op0b58nigs_ida
		if (!empty($focus->conf_op0b58nigs_ida) && isset($_REQUEST['send_invites']) && $_REQUEST['send_invites'] == 1){
			
			//work in sugar 2.3
			//$room_id = 	$focus->openmeetings_meetings->beans[$focus->conf_op0b58nigs_ida]->room_id;	
			
			$sql = "select room_id from openmeetings where id = '" . $focus->conf_op0b58nigs_ida ."'";

			$result = $focus->db->query($sql, true);
			$row = $focus->db->fetchByAssoc($result);
			$room_id = $row['room_id'];	
				
			//$_POST['send_invites'] = 0;					
			$_REQUEST['send_invites'] = '0';
			
			$openmeetings_gateway = new openmeetings_gateway();
			if ($openmeetings_gateway->openmeetings_loginuser()) {

				$admin = new Administration();
	 			$admin->retrieveSettings();
	 			
				global $changeNotify;
				$changeNotify = false;	 			
	 			if($admin->settings['notify_on']) {
	 				$changeNotify = true;
					$admin->saveSetting('notify','on', 0);
				}
				//$notify_user = $focus->get_notification_recipients();

				$descriptionTemp = $focus->description;

				foreach($focus->users_arr as $user_id) {
					$notify_user = new User();
					$notify_user->retrieve($user_id);
					$notify_user->new_assigned_user_name = $notify_user->full_name;
					$GLOBALS['log']->info("Notifications: recipient is $notify_user->new_assigned_user_name");
			
					$invitation_hash = $openmeetings_gateway->getInvitationHash($notify_user->name, $room_id, false, "123", 2, $startDateString, $startTimeString, $endDateString, $endTimeString);
						
					$notify_user->online_meeting_url_temp = "http://".$system_config->settings[info_openmeetings_url].":".
							$system_config->settings[info_openmeetings_http_port].
							"/openmeetings/?" .
							"invitationHash=" .$invitation_hash;
			

					//$admin = new Administration();
		 			//$admin->retrieveSettings();

					$focus->description = $descriptionTemp;
					$tempInvitationHash = "";
					$tempInvitationHash = translate('LBL_EMAIL_TEXT', 'openmeetings')." ". $notify_user->online_meeting_url_temp;
					$focus->description = $focus->description." ".$tempInvitationHash;
					
					$focus->send_assignment_notifications($notify_user, $admin);
				}

			
				foreach($focus->contacts_arr as $contact_id) {
					$notify_user = new Contact();
					$notify_user->retrieve($contact_id);
					$notify_user->new_assigned_user_name = $notify_user->name;
					$GLOBALS['log']->info("Notifications: recipient is $notify_user->new_assigned_user_name");
			
					$invitation_hash = $openmeetings_gateway->getInvitationHash($notify_user->name, $room_id, false, "123", 2, $startDateString, $startTimeString, $endDateString, $endTimeString);
						
					$notify_user->online_meeting_url_temp = "http://".$system_config->settings[info_openmeetings_url].":".
							$system_config->settings[info_openmeetings_http_port].
							"/openmeetings/?" .
							"invitationHash=" .$invitation_hash;
			

					//$admin = new Administration();
		 			//$admin->retrieveSettings();

					$focus->description = $descriptionTemp;
					$tempInvitationHash = "";
					$tempInvitationHash = translate('LBL_EMAIL_TEXT', 'openmeetings')." ". $notify_user->online_meeting_url_temp;
					$focus->description = $focus->description." ".$tempInvitationHash;
					
					$focus->send_assignment_notifications($notify_user, $admin);
				}

				foreach($focus->leads_arr as $lead_id) {
					$notify_user = new Lead();
					$notify_user->retrieve($lead_id);
					$notify_user->new_assigned_user_name = $notify_user->full_name;
					$GLOBALS['log']->info("Notifications: recipient is $notify_user->new_assigned_user_name");

					$invitation_hash = $openmeetings_gateway->getInvitationHash($notify_user->name, $room_id, false, "123", 2, $startDateString, $startTimeString, $endDateString, $endTimeString);
						
					$notify_user->online_meeting_url_temp = "http://".$system_config->settings[info_openmeetings_url].":".
							$system_config->settings[info_openmeetings_http_port].
							"/openmeetings/?" .
							"invitationHash=" .$invitation_hash;
			

					//$admin = new Administration();
		 			//$admin->retrieveSettings();

					$focus->description = $descriptionTemp;
					$tempInvitationHash = "";
					$tempInvitationHash = translate('LBL_EMAIL_TEXT', 'openmeetings')." ". $notify_user->online_meeting_url_temp;
					$focus->description = $focus->description." ".$tempInvitationHash;
					
					$focus->send_assignment_notifications($notify_user, $admin);
				
				}
				
				$focus->description = $descriptionTemp;
				
				//return;
				//echo "send invites: ". $_REQUEST['send_invites'];
				//exit();			
			
			} else {
				die("Could not login User to OpenMeetings, check your OpenMeetings Module Configuration");	
			}
		}
	}




function sendInvitationHash(&$focus, $event) {
		global $sugar_config;
		global $system_config;
		global $current_user;
		
				
		if ($focus->room_id){

			$openmeetings_gateway = new openmeetings_gateway();
			if ($openmeetings_gateway->openmeetings_loginuser()) {
			
				foreach($focus->contacts_arr as $contact_id) {
					$notify_user = new Contact();
					$notify_user->retrieve($contact_id);

					$result = $openmeetings_gateway->openmeetings_sendInvitationHash($current_user->name, 
													"message", "http://".$system_config->settings[info_openmeetings_url].":"
													.$system_config->settings[info_openmeetings_http_port]."/openmeetings/", 
													$notify_user->email1, "Openmeeting Invetation: ".$focus->name, 
													$focus->openmeetings_roomid_c, "", false, "123", 1, "$focus->date_start", 
													"$focus->time_start", "$focus->date_end", "$focus->time_end", 
													$system_config->settings[info_openmeetings_language], true);
		
						
				}

			
			} else {
				die("Could not login User to OpenMeetings, check your OpenMeetings Module Configuration");	
			}
		}
	}
	
	
	function setNotification(&$focus, $event) {
		
		global $changeNotify;		
		$admin = new Administration();
	 	$admin->retrieveSettings();
	 	
	 	if($changeNotify) {	
			$admin->saveSetting('notify','on', 1);
		}
	}
	
	
	function deleteRoom (&$focus, $event){

		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			
			$openmeetings = new stdClass;
			
			$openmeetings->room_id = $focus->room_id;		
			$roomid = $openmeetings_gateway->deleteRoom($openmeetings);					
			return $roomid;
						
		} else {
			die("Could not login User to OpenMeetings, check your OpenMeetings Module Configuration");	
			
		}


	}

}


?>
