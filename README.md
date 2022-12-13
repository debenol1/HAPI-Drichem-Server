# README

HAPI drichem service (HDS) acts as an intermediate socket server between a Fujitsu drichem host (FDH) and a running Elexis environment. The FDH sends the result of a measurement to the HDS which resolves the numeric patient ID and stores the HL7 message as a HL7 file to an output directory which is scanned by Elexis. The HL7 file is imported and assigned automatically by Elexis.

> **_NOTE:_**  If the FDH executes an analysis which should not be imported into Elexis, an alphanumeric patient ID is to be entered in the FDH, The HDS would then ignore the resulting HL7 message. It would only store it to the **UNRESOLABLE_DIRECTORY**.

## Prerequisites
* Fujitsu drichem host (FDH) with Ethernet connection to the HAPI drichem service HDS
* HAPI drichem service (HDS) with Ethernet connection to the Elexis DB

## Build
Before building the package, customize the **/src/main/resources/log4j.properties (log4j.appender.R.File)** file: enter the path to the **LOG_DIRECTORY**. Change to the source directory where the pom.xml resides and enter the following command:

	mvn install
	
You get the running binaries (JAR) in the target directory.

## BASE SETUP
To get the HL7 Drichem server up and running, to the following steps

### Create DB user with read access
To resolve the numeric Elexis patient IDs - which are entered in Fujitsu Drichem before every measurement - a database user needs to be created. The user should only have restricted read access to the Elexis database.  

### Prepare the filesystem
The HAPI drichem service needs several directories to operate. First, an **OUTPUT_DIRECTORY** must be created. The directory must be accessible with write access to store the HL7 output:

	sudo mkdir /PATH_TO_OUTPUT_DIRECTORY
	sudo chmod 0666 /PATH_TO_OUTPUT_DIRECTORY
	
The path to output directory needs to be entered in the Elexis (Datenaustausch/HL7 Dateien). Set the flag **Verzeichnis automatisch überwachen** in order to let Elexis automaticall scan the directory. Next, a directory for unresolvable measurements must be created:

	sudo mkdir /PATH_TO_UNRESOLVABLE_DIRECTORY
	sudo chmod 0666 /PATH_TO_UNRESOLVABLE_DIRECTORY
	
If a faulty Elexis patient ID is entered the according measurement is not going to be pushed forward to the **OUTPUT_DIRECTORY** because the patient can not be resolved. The unresolvable file will be pushed forward to the **UNRESOLVABLE_DIRECTORY** instead.

Next, the **LOG_DIRECTORY** must be created. It must be writeable as well

	sudo mkdir /PATH_TO_LOG_DIRECTORY
	sudo chmod 0666 /PATH_TO_LOG_DIRECTORY
	
Last, the **BASE_DIRECTORY** needs to be created. Here the HAPI drichem service binaries will be stored:

	sudo mkdir /PATH_TO_BASE_DIRECTORY

### Install the HAPI drichem service (HDS) binary
Copy the **HAPI_DRICHEM.jar** to to the **BASE_DIRECTORY**:

	cp HAPI_DRICHEM.jar BASE_DIRECTORY
	
Adjust the file rights in order to make it readable/executable:

	chmod 0755 HAPI_DRICHEM.jar
	
### Create application.properties
Create the application.properties next to HAPI_DRICHEM.jar. Copy the following lines:

	# HAPI Drichem Application Properties
	# - The file needs to be configured and to be placed outside the jar
	# - The file needs to be referenced from the runtime environment
	#
	# Mars 2022 / Olivier Debenath (Framsteg GmbH)
	# Network Configuration
	port=4711
	# Filesystem Configuration
	# Directory which is used by Elexis to scan/detect automatically HL7
	# messages. HAPI Drichem Server stores the messages to this directory
	output.dir=/PATH_TO_OUTPUT_DIR/
	# Directory to which unresolvable HL7 messages are pushed to
	unresolved.dir=/PATH_TO_UNRESOLVED_DIR/
	# HL7 message file extension
	extension=.hl7
	# Beautify
	beautify.message=true
	# Database Configuration
	url=jdbc:postgresql://IP_ADRESS/DATABASE_NAME
	user=DATABASE_USER
	password=DATABASE_PASSWORD
	ssl=false
	# Flag to control the automatic patient assignement.
	# If set to true, the ID string in the HL7 message
	# is assigned automatically by the Elexis HL7 Plugin
	# If set to false, the user must assign the HL7 message
	# manually to the according patient in Elexis
	automatic.patient.assignment=true
	# Flag to control the treatment of unresolvable HL7 messages
	# Unresolvable HL7 messages do not contain resolvable
	# patient IDs. If set to true a copy of the unresolvable HL7 message is
	# stored in the local filesystem. If set to false the unresolvable
	# HL7 message is skipped
	push.unresolved.to.filesystem=true
	# Labor ID which is used by Elexis
	labor.id=Labor intern DriChem
	# Location of the patient ID within the HL7 message
	# The ID is being resolved against the Elexis database
	patient.id.column=3
	# HL7 Application
	app.messageType=OUL
	app.triggerEvent=R22
	# HL7/SPM Segment
	spm.segment.id=SPM
	# HL7/MSG Segment
	msh.segment.id=MSH
	# HL7/PID Segment bare bone. The string is being filled by the 
	# HAPI Drichem Server.
	pid.segment=PID|{0}||||{1}||{2}||||||||||||
	# HAPI Drichem LOINCs
	loincs=14749-6^,14937-7^,22664-7^,718-7^,14933-6^,14647-2^,16362-6^,14927-8^,14682-9^,2885-2^,1751-7^,14631-6^,2000-8^,14879-1^,14629-0^,14646-4^,2601-3^,2324-2^,1920-8^,1742-6^,2157-6^,14804-9^,6768-6^,2187-3^,32673-6^,2098-2^,14804-9^,6768-6^,1798-8^,2572-6^,2028-9^,1988-5^,2951-2^,2823-3^,2075-0^,39469-2^,70204-3^,10834-0^,1759-0^,3097-3^,39783-6^,1916-6^,28003-2^,33037-3^
	# HAPI Drichem LOINCs descrption
	14749-6^=Alanine aminotransferase^
	14937-7^=Urea nitrogen^
	22664-7^=Urea^
	718-7^=Alanine aminotransferase^
	14933-6^=Urate^
	14647-2^=Cholesterol^
	16362-6^=Ammonia^
	14927-8^=Triglyceride^
	14682-9^=Creatinine^
	2885-2^=Protein^
	1751-7^=Albumin^
	14631-6^=Bilirubin total^
	2000-8^=Calc^
	14879-1^=Phosphate^
	14629-0^=Bilirubin direct^
	14646-4^=Cholesterol in HDL^
	2601-3^=Magnesium^
	2324-2^=Gamma glutamyl transferase^
	1920-8^=Aspartate aminotransferase^
	1742-6^=Alanine aminotransferase^
	2157-6^=Creatine kinase^
	14804-9^=Lactate dehydrogenase^
	6768-6^=Alkaline phosphatase^
	2187-3^=Cytosol aminopeptidase^
	32673-6^=Creatine kinase MB^
	2098-2^=Cholinesterase^
	14804-9^=Lactate dehydrogenase^
	6768-6^=Alkaline phosphatase^
	1798-8^=Amylase^
	2572-6^=Lipoprotein lipase^
	2028-9^=Alanine aminotransferase
	1988-5^=Alanine aminotransferase
	2951-2^=Alanine aminotransferase
	2823-3^=Alanine aminotransferase
	2075-0^=Alanine aminotransferase
	39469-2^=Alanine aminotransferase
	70204-3^=Alanine aminotransferase
	10834-0^=Alanine aminotransferase
	1759-0^=Alanine aminotransferase
	3097-3^=Alanine aminotransferase
	39783-6^=Alanine aminotransferase
	1916-6^=Alanine aminotransferase
	28003-2^=Alanine aminotransferase
	33037-3^=Alanine aminotransferase
	
### Adjust the significant properties
The following properties must be configured. **LEAVE THE OTHERS PROPERTIES JUST AS THEY ARE!**

Define the TCP/IP port that should be used by the HAPI drichem service:

	# - The file needs to be configured and to be placed outside the jar
	# - The file needs to be referenced from the runtime environment
	# Network Configuration
	port=4711
	
Next, the pathes which were created before must be entered here:

	# Filesystem Configuration
	# Directory which is used by Elexis to scan/detect automatically HL7
	# messages. HAPI Drichem Server stores the messages to this directory
	output.dir=/PATH_TO_OUTPUT_DIR/
	# Directory to which unresolvable HL7 messages are pushed to
	unresolved.dir=/PATH_TO_UNRESOLVED_DIR/

Now, the database connection must be configured in order to resolve the numeric Elexis patient IDs. Use the database user/password which were created at the bginning:
 	
	# Database Configuration
	url=jdbc:postgresql://IP_ADRESS/DATABASE_NAME
	user=DATABASE_USER
	password=DATABASE_PASSWORD

The next configuration is optional. If you want Elexis to assign the measurement automatically, leave the original configuration. Otherwise set the flag to false. HAPI drichem service would the switch prename and name to force Elexis to ask to which patient the measurement is to be assigned to:

	# Flag to control the automatic patient assignement.
	# If set to true, the ID string in the HL7 message
	# is assigned automatically by the Elexis HL7 Plugin
	# If set to false, the user must assign the HL7 message
	# manually to the according patient in Elexis
	automatic.patient.assignment=true
		
### Create HAPI drichem service
In order to get the HAPI drichem service started automaticallv, create a service configuration file:

	sudo vi /etc/systemd/system/hapi.drichem.service
	
Enter the following lines:

	[Unit]
	Description=HAPI Drichem Socket Server

	[Service]
	User=USER_WITH_SUDOERS_RIGHTS
	WorkingDirectory=/BASE_DIRECTORY
	ExecStart=java -jar hapi.drichem.jar application.properties
	Restart=always
	StartLimitBurst=0

	[Install]
	WantedBy=multi-user.target
	
Activate the service:

	sudo systemctl daemon-reload
	sudo systemctl start hapi.drichem.service
	sudo systemctl enable hapi.drichem.service
	
If you want to disable the service enter:

	sudo systemctl disable hapi.drichem.service

## Initial start
Now the HAPI drichem service can be started for the first time:

	sudo systemctl start hapi.drichem.service
	
HAPI drichem service is started an waits for HL7 messages, coming from Fuijitsu Drichem. If you want to check the log, change to the LOG_DIR and tail it:

	sudo tail -f /PATH_TO_LOG_DIRECTORY/hapi_drichem.log
	
You then will see something like:

	INFO main ca.uhn.hl7v2.VersionLogger - HAPI version is: 2.2
	INFO main ca.uhn.hl7v2.VersionLogger - Default Structure libraries found for HL7 versions 2.1, 2.5.1, 
	INFO main ch.framsteg.hl7.drichem.server.elexis.DrichemMessageReceiver - Creating context
	INFO main ch.framsteg.hl7.drichem.server.elexis.DrichemMessageReceiver - Creating server
	INFO main ch.framsteg.hl7.drichem.server.elexis.DrichemMessageReceiver - Creating receiving application
	INFO main ch.framsteg.hl7.drichem.server.elexis.DrichemMessageReceiver - Registering OUL R22 application
	INFO main ch.framsteg.hl7.drichem.server.elexis.DrichemMessageReceiver - Registering connection DrichemConnectionListener()
	INFO main ch.framsteg.hl7.drichem.server.elexis.DrichemMessageReceiver - Setting DrichemExceptionHandler
	INFO hapi-worker-1 ca.uhn.hl7v2.app.HL7Service - Starting ConnectionCleaner service
	INFO hapi-worker-1 ca.uhn.hl7v2.app.SimpleServer - Starting SimpleServer running on port 4711
	INFO main ch.framsteg.hl7.drichem.server.elexis.DrichemMessageReceiver - HAPI Drichem Server started and waiting for 			requests...

> **_NOTE:_** If HAPI drichem service (HDS) does not start, most probably there is a misconfiguration of the filesystem. Double check the pathes and their read/write access.

The HAPI drichem service (HDS) is now ready an waiting for HL7 messages. If you want to test it without a Fujitsu drichem host (FDH) you can send a dummy HL7 message using the HL7 drichem client (for testing purposes only!)

