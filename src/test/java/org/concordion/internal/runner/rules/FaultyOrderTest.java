package org.concordion.internal.runner.rules;


import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class FaultyOrderTest {
    private static final int MONGO_PORT_FOR_TEST = 55555;
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public ManagedMongoDb managedMongo = new ManagedMongoDb();
    @Rule 
    public InMemoryMongoDb inMemoryDB = new InMemoryMongoDb();
    @Rule
    public DropwizardAppRule<TestConfiguration> dyamandBackend = new DropwizardAppRule<TestConfiguration>();
    
    public void test() {
    }
}
