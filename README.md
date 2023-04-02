NAVIGATE TO ./src DIRECTORY BEFORE RUNNING ANY SCRIPTS

to build the client, run                    ./build.sh              <br>
to build&test client on S1testConfigs,      ./runAllTest            <br>
to run the client, no script, AFTER BUILD   java ./Main.class       <br>
<br><br>

I will include all of the final .class files so don't worry about building the app. If the app fails to run, try re-building it with the build script but it should work. <br>
Main.class is the compiled entry point for the program and should be treated as the ds-client. <br>

usage to mirror ds-client ( ./ds-client -a lrr ) ==> <strong>java Main -a lrr</strong>