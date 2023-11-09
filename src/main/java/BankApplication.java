import com.luxoft.bankapp.exceptions.ActiveAccountNotSet;
import com.luxoft.bankapp.model.AbstractAccount;
import com.luxoft.bankapp.model.CheckingAccount;
import com.luxoft.bankapp.model.Client;
import com.luxoft.bankapp.model.Client.Gender;
import com.luxoft.bankapp.model.SavingAccount;
import com.luxoft.bankapp.service.BankReportService;
import com.luxoft.bankapp.service.Banking;
import com.luxoft.bankapp.service.storage.ClientRepository;
import com.luxoft.bankapp.service.storage.MapClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan("com.luxoft.bankapp")
@PropertySource("classpath:clients.properties")
public class BankApplication {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Environment environment;


    private static final String[] CLIENT_NAMES =
            {"Jonny Bravo", "Adam Budzinski", "Anna Smith"};

    @Bean("placeholderConfigurer")
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("clients.properties"));
        return propertySourcesPlaceholderConfigurer;
    }

    public static void main(String[] args) {

//        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml", "test-clients.xml");

        ApplicationContext context = new AnnotationConfigApplicationContext(BankApplication.class);
        ClientRepository repository = new MapClientRepository();
        Banking banking = initialize(context);

        workWithExistingClients(context);

        bankingServiceDemo(context);

        bankReportsDemo(context);
    }

    public static void bankReportsDemo(ApplicationContext context) {

        System.out.println("\n=== Using BankReportService ===\n");

//        BankReportService reportService = (BankReportService) context.getBean("BankReportServiceImpl");
        BankReportService reportService = context.getBean(BankReportService.class);

//        reportService.setRepository(repository);

        System.out.println("Number of clients: " + reportService.getNumberOfBankClients());

        System.out.println("Number of accounts: " + reportService.getAccountsNumber());

        System.out.println("Bank Credit Sum: " + reportService.getBankCreditSum());
    }

    public static void bankingServiceDemo(ApplicationContext context) {

        System.out.println("\n=== Initialization using Banking implementation ===\n");

        Banking banking = context.getBean(Banking.class);
        Client anna = new Client(CLIENT_NAMES[2], Gender.FEMALE);
        anna = banking.addClient(anna);

        AbstractAccount saving = banking.createAccount(anna, SavingAccount.class);
        saving.deposit(1000);

        banking.updateAccount(anna, saving);

        AbstractAccount checking = banking.createAccount(anna, CheckingAccount.class);
        checking.deposit(3000);

        banking.updateAccount(anna, checking);

        banking.getAllAccounts(anna).stream().forEach(System.out::println);
    }

    public static void workWithExistingClients(ApplicationContext context) {

        System.out.println("\n=======================================");
        System.out.println("\n===== Work with existing clients ======");

        Banking banking = context.getBean(Banking.class);
        Client jonny = banking.getClient(CLIENT_NAMES[0]);

        try {

            jonny.deposit(5_000);

        } catch (ActiveAccountNotSet e) {

            System.out.println(e.getMessage());

            jonny.setDefaultActiveAccountIfNotSet();
            jonny.deposit(5_000);
        }

        System.out.println(jonny);

        Client adam = banking.getClient(CLIENT_NAMES[1]);
        adam.setDefaultActiveAccountIfNotSet();

        adam.withdraw(1500);

        double balance = adam.getBalance();
        System.out.println("\n" + adam.getName() + ", current balance: " + balance);

        banking.transferMoney(jonny, adam, 1000);

        System.out.println("\n=======================================");
        banking.getClients().forEach(System.out::println);
    }

    /*
     * Method that creates a few clients and initializes them with sample values
     */
    public static Banking initialize(ApplicationContext context) {

        Banking banking = (Banking) context.getBean("BankingImpl");
//        banking.setRepository(repository);

//        Client client_1 = new Client(CLIENT_NAMES[0], Gender.MALE);
        Client client_1 = (Client) context.getBean("client1");

//        AbstractAccount savingAccount = new SavingAccount(1000);
//        client_1.addAccount(savingAccount);
//
//        AbstractAccount checkingAccount = new CheckingAccount(1000);
//        client_1.addAccount(checkingAccount);

//        Client client_2 = new Client(CLIENT_NAMES[1], Gender.MALE);
        Client client_2 = (Client) context.getBean("client2");

//        AbstractAccount checking = new CheckingAccount(1500);
//        client_2.addAccount(checking);

        banking.addClient(client_1);
        banking.addClient(client_2);

        return banking;
    }
}
