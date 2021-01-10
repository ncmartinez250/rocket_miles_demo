package com.example.demo;

import com.example.demo.model.CashierRegistry;
import com.example.demo.repository.CashierRegistryRepository;
import com.example.demo.service.CashierRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Profile("dev")
@Component
public class Runner implements CommandLineRunner {

    final static String INSUFFICIENT_DATA = "insufficient data";
    final static String UNKNOWN_OPTIONS = "unknown options";

    @Autowired
    private CashierRegistryService service;

    @Value("${demo.default.registry.id}")
    private Long defaultRegistryId;

    @Override
    public void run(String... args) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.println("Starting...");
        CashierRegistry registry = service.setUpCashierRegistry(defaultRegistryId);
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("...");
            System.out.println("Please enter command:");
            String command = in.nextLine();
            String[] input = command.split(" ", 2);
            String mainCommand = input[0];
            try {
                switch(mainCommand) {
                    case "show":
                        if(input.length != 1) {
                            System.out.println(UNKNOWN_OPTIONS);
                            break;
                        }
                        String showState = service.showCashierRegistryState(registry);
                        System.out.println(showState);
                        break;
                    case "put":
                        if(input.length != 2) {
                            System.out.println(INSUFFICIENT_DATA);
                            break;
                        }
                        String putState = service.putBillsForEachDenomination(registry, input[1]);
                        System.out.println(putState);
                        break;
                    case "take":
                        if(input.length != 2) {
                            System.out.println(INSUFFICIENT_DATA);
                            break;
                        }
                        String takeState = service.takeBillsForEachDenomination(registry, input[1]);
                        System.out.println(takeState);
                        break;
                    case "change":
                        if(input.length != 2) {
                            System.out.println(INSUFFICIENT_DATA);
                            break;
                        }
                        String changeState = service.getChangeForValue(registry, input[1]);
                        System.out.println(changeState);
                        break;
                    case "quit":
                        if(input.length != 1) {
                            System.out.println(UNKNOWN_OPTIONS);
                            break;
                        }
                        System.out.println("bye");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("unknown command");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("data in wrong format");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
