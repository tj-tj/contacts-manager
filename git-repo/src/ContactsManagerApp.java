import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ContactsManagerApp {
    private static Scanner sc = new Scanner(System.in);
    private static String dataDir = "./git-repo/data";
    private static String contactsTxt = "contacts.txt";


    public static void main(String[] args) {
        initApp();
    }


    public static void initApp() {
        boolean userContinue = true;

        Path dataDirectory = Paths.get(dataDir);
        Path contactsFile = Paths.get(dataDir, contactsTxt);

        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            if (! Files.exists(contactsFile)) {
                Files.createFile(contactsFile);
            }
        } catch (IOException e) {
            System.out.printf("ERROR: %s\n", e);
        }

        Map<String, Contact> contactsList = new HashMap<>();

        try {
            List<String> contactLines = Files.readAllLines(contactsFile);
            for (String line: contactLines) {
                Contact contact = new Contact();
                String[] tokens = line.split(",");
                contact.setName(tokens[0]);
                contact.setPhoneNo(tokens[1]);
                contactsList.putIfAbsent(contact.getName(), contact);
            }
        } catch (IOException e) {
            System.out.printf("ERROR: %s", e);
        }


        System.out.println();
        System.out.println("\n  Welcome to the Contacts Manager!\n|==================================|\n");

        do {

            switch (showMenu()) {
                case 1:
                    showContacts(contactsList);
                    break;
                case 2:
                    Contact dude = addContact();
                    contactsList.putIfAbsent(dude.getName(), dude);
                    break;
                case 3:
                    searchContact(contactsList);
                    break;
                case 4:
                    deleteContact(contactsList);
                    break;
                case 5:
                    System.out.println("\n Thanks for using the Contacts Manager!\nGoodbye!");
                    userContinue = false;
            }
        } while (userContinue);
        overwriteFile(contactsList);
    }


    public static int showMenu() {
        System.out.println("What would you like to do?\n");

        System.out.println("1. View contacts.");
        System.out.println("2. Add a new contact.");
        System.out.println("3. Search a contact by name.");
        System.out.println("4. Delete an existing contact.");
        System.out.println("5. Exit.");
        System.out.print("\nEnter an option (choose 1, 2, 3, 4, or 5): ");
        return sc.nextInt();
    }


    public static void showContacts(Map<String, Contact> contactsList) {
        System.out.println("\n\n#######################################");
        System.out.println("#        Name        |  Phone Number  #");
        System.out.println("# ----------------------------------- #");
        System.out.println("#                    |                #");

        // TreeMap to store values of HashMap
        TreeMap<String, Contact> sortedContacts = new TreeMap<>();

        // Copy all data from hashMap into TreeMap
        sortedContacts.putAll(contactsList);

        // Display the TreeMap which is naturally sortedContacts
        for (Map.Entry<String, Contact> entry : sortedContacts.entrySet()) {
            System.out.printf("# %-18s | %-14s #\n",
                    entry.getKey(),
                    entry.getValue().getPhoneNo());
        }

        System.out.println("#######################################\n");
    }


    public static Contact addContact() {
        sc.nextLine();
        boolean overwriteContact;
        System.out.print("Enter name (e.g. \"John Smith\"): ");
        String name = sc.nextLine();
        System.out.print("Enter phone number (e.g. \"1234567890\"): ");
        String number = sc.nextLine();
        if (number.length() == 11) {
            number = number.substring(0,1) +
                    "-" + number.substring(1,4) +
                    "-" + number.substring(4,7) +
                    "-" + number.substring(7);
        } else if (number.length() == 10) {
            number = number.substring(0,3) +
                    "-" + number.substring(3,6) +
                    "-" + number.substring(6);
        } else {
            number = number.substring(0,3) +
                    "-" + number.substring(3);
        }


        System.out.println("New Contact Added.\n\n");
        return new Contact(name, number);
    }


    public static void searchContact(Map<String, Contact> contactsList) {
        sc.nextLine();

        System.out.print("Whom are you searching for: ");
        String userSearch = sc.nextLine();
        System.out.println("\n\n#######################################");
        System.out.println("#        Name        |  Phone Number  #");
        System.out.println("# ----------------------------------- #");
        System.out.println("#                    |                #");
        for (Contact person: contactsList.values()) {
            if (person.getName().contains(userSearch)) {
                System.out.printf("# %-18s | %-14s #\n",
                        person.getName(),
                        person.getPhoneNo());
            }
        }
        System.out.println("#######################################\n");
    }


    public static void deleteContact(Map<String, Contact> contactsList) {
        sc.nextLine();
        boolean goodToRemove = false;


        System.out.print("Enter the name of the person you wish to delete (CASE SENSITIVE!): ");
        String userDelete = sc.nextLine();
        System.out.println("\n\n#######################################");
        System.out.println("#        Name        |  Phone Number  #");
        System.out.println("# ----------------------------------- #");
        System.out.println("#                    |                #");
        for (Contact person: contactsList.values()) {
            if (person.getName().equals(userDelete)) {
                System.out.printf("# %-18s | %-14s #\n",
                        person.getName(),
                        person.getPhoneNo());
                goodToRemove = true;
            }
        }
        System.out.println("#######################################\n");
        if (goodToRemove) {
            contactsList.remove(userDelete);
        }
        System.out.printf("\n\"%s\" removed.\n\n", userDelete);
    }


    public static void overwriteFile(Map<String,Contact> contacts) {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(dataDir, contactsTxt);

        for (Contact person: contacts.values()) {
            String line = person.getName() + "," + person.getPhoneNo();
            lines.add(line);
        }
        try {
            Files.write(path, lines);
        } catch (IOException e) {
            System.out.printf("ERROR: %s", e);
        }
    }
}
