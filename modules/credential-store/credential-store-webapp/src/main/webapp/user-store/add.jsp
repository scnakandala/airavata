<%--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page import="org.apache.airavata.credentialstore.local.LocalUserStore" %>

<html>

<head>
    <script language="javascript" type="text/javascript">
        function validatePassword(fld1name, regString) {
            var stringValue = document.getElementsByName(fld1name)[0].value;
            var errorMessage = "";
            if(regString != "null" && !stringValue.match(new RegExp(regString))){
                errorMessage = "Password does not meet minimum requirements. Password length must be at least 6 " +
                        "characters.";
                return errorMessage;
            }else if(regString != "null" && stringValue == ''){
                return errorMessage;
            }

            if (stringValue == '') {
                errorMessage = "Empty passwords are not allowed. Please enter a valid password";
                return errorMessage;
            }

            return errorMessage;
        }

        function validateUsername(fld1name) {
            var stringValue = document.getElementsByName(fld1name)[0].value;
            var errorMessage = "";

            if (stringValue == '') {
                errorMessage = "Empty user names are not allowed. Please enter a valid user name.";
                return errorMessage;
            }

            return errorMessage;
        }

        function checkPasswordsMatching(fld1name, fld2name) {

            var stringValue1 = document.getElementsByName(fld1name)[0].value;
            var stringValue2 = document.getElementsByName(fld2name)[0].value;
            var errorMessage = "";

            if (stringValue1 != stringValue2) {
                errorMessage = "Confirm password does not match with the password. Please re-enter passwords.";
                return errorMessage;
            }

            return errorMessage;

        }

        function validate() {
            var reason = "";

            reason = validateUsername("username");

            if (reason != "") {
                alert(reason);
                return false;
            }

            reason = validatePassword("newPassword", <%=LocalUserStore.getPasswordRegularExpression()%>);

            if (reason != "") {
                alert(reason);
                document.getElementsByName("newPassword")[0].clear();
                return false;
            }

            reason = checkPasswordsMatching("newPassword", "confirmPassword");

            if (reason != "") {
                alert(reason);
                document.getElementsByName("newPassword")[0].clear();
                document.getElementsByName("confirmPassword")[0].clear();
                return false;
            }

            return true;
        }

        function doProcess() {
            if (validate() == true) {
                document.registration.submit();
            }
        }


    </script>
</head>

<body>
<img src="../images/airavata-logo-2.png">
<h2>Airavata Credential Store - Local User Store</h2>
<p><b>Manage Local User Store - Add New User</b></p>

<form action="index.jsp" name="registration" method="POST">

    <input type="hidden" name="operation" value="addUser">
    <table>
        <tr>
            <td>User Name</td>
            <td><input type="text" name="username" maxlength="150"></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="password" name="newPassword"/></td>
        </tr>
        <tr>
            <td>Re-Type Password</td>
            <td><input type="password" name="confirmPassword"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td><input type="button" value="Add" onclick= 'doProcess()'></td>
            <td><a href="index.jsp"><input type="button" value="Cancel" name="Cancel"/> </a> </td>
        </tr>
    </table>

</form>

</body>
</html>