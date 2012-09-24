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
// created: 2011-09-04 16:40:38
$dictionary["Meeting"]["fields"]["openmeetings_meetings"] = array (
  'name' => 'openmeetings_meetings',
  'type' => 'link',
  'relationship' => 'openmeetings_meetings',
  'source' => 'non-db',
  'vname' => 'LBL_openmeetings_MEETINGS_FROM_openmeetings_TITLE',
  'id_name' => 'conf_op0b58nigs_ida',
);
$dictionary["Meeting"]["fields"]["conf_opeigs_meetings_name"] = array (
  'name' => 'conf_opeigs_meetings_name',
  'type' => 'relate',
  'source' => 'non-db',
  'vname' => 'LBL_openmeetings_MEETINGS_FROM_openmeetings_TITLE',
  'save' => true,
  'id_name' => 'conf_op0b58nigs_ida',
  'link' => 'openmeetings_meetings',
  'table' => 'openmeetings',
  'module' => 'openmeetings',
  'rname' => 'name',
);
$dictionary["Meeting"]["fields"]["conf_op0b58nigs_ida"] = array (
  'name' => 'conf_op0b58nigs_ida',
  'type' => 'link',
  'relationship' => 'openmeetings_meetings',
  'source' => 'non-db',
  'reportable' => false,
  'side' => 'right',
  'vname' => 'LBL_openmeetings_MEETINGS_FROM_MEETINGS_TITLE',
);