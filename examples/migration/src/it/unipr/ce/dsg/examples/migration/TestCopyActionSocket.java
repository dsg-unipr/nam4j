package it.unipr.ce.dsg.examples.migration;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;

public class TestCopyActionSocket extends NetworkedAutonomicMachine {

	public TestCopyActionSocket(String configuration) {
		super(10, "examples/migration", 3, Platform.DESKTOP);
		this.setId("migration");
	}

	public static void main(String[] args) {

		TestCopyActionSocket migration = new TestCopyActionSocket(args[0]);

		if (args[0].equals("SERVER")) {
			migration.startMobilityAction();
		} else if (args[0].equals("CLIENT")) {

			// Request Chord FM
//			migration.startCopyAction("ChordFunctionalModule",
//					null,
//					null,
//					Platform.DESKTOP,
//					null);

			// Request a FM and a Service
//			migration.startCopyAction("TestFunctionalModule",
//					new String[] { "TestService" },
//					new String[] { "serviceId" },
//					Platform.DESKTOP,
//					null);
			
			// Request a FM
			migration.startCopyAction("TestFunctionalModule",
					null,
					null,
					Platform.DESKTOP,
					null);
		}
	}

}
