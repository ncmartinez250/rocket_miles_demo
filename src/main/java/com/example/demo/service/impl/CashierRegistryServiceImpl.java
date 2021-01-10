package com.example.demo.service.impl;

import com.example.demo.model.CashierRegistry;
import com.example.demo.repository.CashierRegistryRepository;
import com.example.demo.service.CashierRegistryService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.IntStream;

@Service
public class CashierRegistryServiceImpl implements CashierRegistryService {

    final static int NUMBER_OF_DENOMINATIONS = 5;
    final static int TWENTY_DOLLAR_INDEX = 0;
    final static int TEN_DOLLAR_INDEX = 1;
    final static int FIVE_DOLLAR_INDEX = 2;
    final static int TWO_DOLLAR_INDEX = 3;
    final static int ONE_DOLLAR_INDEX = 4;
    public final static String NO_CHANGE = "sorry no change available";

    @Autowired
    private CashierRegistryRepository repo;

    @Override
    public CashierRegistry setUpCashierRegistry(Long id) {
        if(repo.existsById(id)) {
            return repo.findById(id).get();
        }

        CashierRegistry newRegistry = new CashierRegistry();
        newRegistry.setId(id);
        newRegistry.setZeroBillsForEachDenomination();
        return repo.save(newRegistry);

    }

    @Override
    public String showCashierRegistryState(CashierRegistry registry) {
        return registry.getState();
    }

    @Override
    public String putBillsForEachDenomination(CashierRegistry registry, String input) {
        Integer[] denominations = getIntegerValuesForInput(input);
        registry.addTwentyDollarBill(denominations[TWENTY_DOLLAR_INDEX])
                .addTenDollarBill(denominations[TEN_DOLLAR_INDEX])
                .addFiveDollarBill(denominations[FIVE_DOLLAR_INDEX])
                .addTwoDollarBill(denominations[TWO_DOLLAR_INDEX])
                .addOneDollarBill(denominations[ONE_DOLLAR_INDEX]);
        repo.save(registry);
        return registry.getState();
    }

    @Override
    public String takeBillsForEachDenomination(CashierRegistry registry, String input) {
        Integer[] denominations = getIntegerValuesForInput(input);
        if(!isInputValidForTakeBills(registry, denominations)) {
            throw new IllegalArgumentException("bills are insufficient for the specified input");
        }
        registry.addTwentyDollarBill(-denominations[TWENTY_DOLLAR_INDEX])
                .addTenDollarBill(-denominations[TEN_DOLLAR_INDEX])
                .addFiveDollarBill(-denominations[FIVE_DOLLAR_INDEX])
                .addTwoDollarBill(-denominations[TWO_DOLLAR_INDEX])
                .addOneDollarBill(-denominations[ONE_DOLLAR_INDEX]);
        repo.save(registry);
        return registry.getState();
    }

    @Override
    public String getChangeForValue(CashierRegistry registry, String value) {
        Integer intValue = Integer.parseInt(value);
        if(intValue > registry.getTotal()) {
            return NO_CHANGE;
        } else if(intValue < 0) {
            throw new IllegalArgumentException();
        }

        String change = calculateChange(registry, intValue);
        if(!change.equals(NO_CHANGE)) {
            takeBillsForEachDenomination(registry, change);
            repo.save(registry);
        }

        return change;
    }

    private String calculateChange(CashierRegistry registry, Integer value) {
        int[] changeDenominations = {0, 0, 0, 0, 0};
        DenominationReference reference;
        boolean isCalculated = false;
        int indexToCheck = -1;
        while(true) {
            Integer total = value;
            changeDenominations[TWENTY_DOLLAR_INDEX] = indexToCheck >= TWENTY_DOLLAR_INDEX ?
                    changeDenominations[TWENTY_DOLLAR_INDEX] : getDenominationCount(total, 20.0, registry.getTwentyDollarCount());
            total -= changeDenominations[TWENTY_DOLLAR_INDEX] * 20;

            changeDenominations[TEN_DOLLAR_INDEX] = indexToCheck >= TEN_DOLLAR_INDEX ?
                    changeDenominations[TEN_DOLLAR_INDEX] : getDenominationCount(total, 10.0, registry.getTenDollarCount());
            total -= changeDenominations[TEN_DOLLAR_INDEX] * 10;

            changeDenominations[FIVE_DOLLAR_INDEX] = indexToCheck >= FIVE_DOLLAR_INDEX ?
                    changeDenominations[FIVE_DOLLAR_INDEX] : getDenominationCount(total, 5.0, registry.getFiveDollarCount());
            total -= changeDenominations[FIVE_DOLLAR_INDEX] * 5;

            changeDenominations[TWO_DOLLAR_INDEX] = indexToCheck >= TWO_DOLLAR_INDEX ?
                    changeDenominations[TWO_DOLLAR_INDEX] : getDenominationCount(total, 2.0, registry.getTwoDollarCount());
            total -= changeDenominations[TWO_DOLLAR_INDEX] * 2;

            changeDenominations[ONE_DOLLAR_INDEX] = indexToCheck >= ONE_DOLLAR_INDEX ?
                    changeDenominations[ONE_DOLLAR_INDEX] : getDenominationCount(total, 1.0, registry.getOneDollarCount());
            total -= changeDenominations[ONE_DOLLAR_INDEX] * 1;

            if(total == 0) {
                isCalculated = true;
                break;
            } else {
                reference = updateChangeDenominations(changeDenominations);
                changeDenominations = reference.getChangeDenomination();
                indexToCheck = reference.getIndexChanged();
                // set last index to zero for easy summation
                changeDenominations[NUMBER_OF_DENOMINATIONS - 1] = 0;
                Integer sumOfDenominations = IntStream.of(changeDenominations).sum();
                // check if last index changed is the two dollar index
                // and if sum of denominations is zero
                // to declare no calculation was found
                if(indexToCheck == TWO_DOLLAR_INDEX && sumOfDenominations == 0) {
                    break;
                }
            }
        }

        if(!isCalculated) {
            return NO_CHANGE;
        }

        return String.format("%d %d %d %d %d", changeDenominations[0], changeDenominations[1],
                changeDenominations[2], changeDenominations[3], changeDenominations[4]);
    }


    private DenominationReference updateChangeDenominations(int[] currentChangeDenominations) {
        // minus 1 because we don't need to change ones denomination
        // and minus 1 because we start with length - 1 index
        int index = NUMBER_OF_DENOMINATIONS - 2;
        int indexToCheck = 0;
        for(int i=index; i>=0; i--) {
            if(currentChangeDenominations[i] > 0) {
                currentChangeDenominations[i] -= 1;
                indexToCheck = i;
                break;
            }
        }

        return new DenominationReference(currentChangeDenominations, indexToCheck);
    }

    private Integer getDenominationCount(Integer total, Double denomination, Integer denominationCount) {
        Integer dollarCount = (int) Math.floor(total/denomination);
        return dollarCount > denominationCount
                ? denominationCount : dollarCount;
    }

    private Integer[] getIntegerValuesForInput(String input) {
        Integer[] denominations = Arrays.stream(input.split(" "))
                .mapToInt(inputElement -> Integer.parseInt(inputElement))
                .filter(num -> num >= 0)
                .boxed().toArray( Integer[]::new );
        if (denominations.length != NUMBER_OF_DENOMINATIONS) {
            throw new IllegalArgumentException("input not valid");
        }
        return denominations;
    }

    private boolean isInputValidForTakeBills(CashierRegistry registry, Integer[] denominations) {
        for(int i=0; i<NUMBER_OF_DENOMINATIONS; i++) {
            switch (i) {
                case TWENTY_DOLLAR_INDEX:
                    if(registry.getTwentyDollarCount() < denominations[i]) {
                        return false;
                    }
                    break;
                case TEN_DOLLAR_INDEX:
                    if(registry.getTenDollarCount() < denominations[i]) {
                        return false;
                    }
                    break;
                case FIVE_DOLLAR_INDEX:
                    if(registry.getFiveDollarCount() < denominations[i]) {
                        return false;
                    }
                    break;
                case TWO_DOLLAR_INDEX:
                    if(registry.getTwoDollarCount() < denominations[i]) {
                        return false;
                    }
                    break;
                case ONE_DOLLAR_INDEX:
                    if(registry.getOneDollarCount() < denominations[i]) {
                        return false;
                    }
                    break;
            }
        }

        return true;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class DenominationReference {
        private int[] changeDenomination;
        private int indexChanged;
    }
}
