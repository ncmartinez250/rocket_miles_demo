package com.example.demo.service;

import com.example.demo.model.CashierRegistry;
import com.example.demo.repository.CashierRegistryRepository;
import com.example.demo.service.impl.CashierRegistryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:/test.sql")
@ActiveProfiles("test")
@SpringBootTest()
public class CashierRegistryServiceTests {

    @Autowired
    private CashierRegistryService registryService;

    @Autowired
    private CashierRegistryRepository registryRepo;

    // test setting up registry that is existing
    @Test
    void testSetUpCashierRegistryWithExistingId() {
        CashierRegistry registry = registryService.setUpCashierRegistry(1L);
        Assertions.assertThat(registry.getId()).isEqualTo(1L);
    }

    // test show registry state that is existing
    @Test
    void testShowOfExistingRegistryState() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        String state = registryService.showCashierRegistryState(registry);
        Assertions.assertThat(state).isEqualTo("$68 1 2 3 4 5");
    }

    // test show registry state that is existing
    @Test
    void testShowOfNullRegistryState() {
        Assertions.assertThatNullPointerException().isThrownBy(
                () -> registryService.showCashierRegistryState(null)
        );
    }


    @Test
    void testPutValidBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        String state = registryService.putBillsForEachDenomination(registry, "4 3 2 1 0");
        Assertions.assertThat(state).isEqualTo("$190 5 5 5 5 5");
    }

    @Test
    void testPutNegativeBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        Assertions.assertThatIllegalArgumentException().isThrownBy(
                () -> registryService.putBillsForEachDenomination(registry, "-5 -3 -1 1 0")
        );
    }

    @Test
    void testPutInvalidBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        Assertions.assertThatIllegalArgumentException().isThrownBy(
                () -> registryService.putBillsForEachDenomination(registry, "A % -1 [] 0.1")
        );
    }

    @Test
    void testPutZeroBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        String state = registryService.putBillsForEachDenomination(registry, "0 0 0 0 0");
        Assertions.assertThat(state).isEqualTo("$68 1 2 3 4 5");
    }

    @Test
    void testTakeValidBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        String state = registryService.takeBillsForEachDenomination(registry, "1 2 3 4 5");
        Assertions.assertThat(state).isEqualTo("$0 0 0 0 0 0");
    }

    @Test
    void testTakeExceedingBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        Assertions.assertThatIllegalArgumentException().isThrownBy(
                () -> registryService.takeBillsForEachDenomination(registry, "2 3 4 5 6")
        );
    }

    @Test
    void testTakeNegativeBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        Assertions.assertThatIllegalArgumentException().isThrownBy(
                () -> registryService.takeBillsForEachDenomination(registry, "-5 -3 -1 1 0")
        );
    }

    @Test
    void testTakeInvalidBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        Assertions.assertThatIllegalArgumentException().isThrownBy(
                () -> registryService.takeBillsForEachDenomination(registry, "A % -1 [] 0.1")
        );
    }

    @Test
    void testTakeZeroBillsForEachDenomination() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        String state = registryService.takeBillsForEachDenomination(registry, "0 0 0 0 0");
        Assertions.assertThat(state).isEqualTo("$68 1 2 3 4 5");
    }

    @Test
    void testChangeForValidValue() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        String change = registryService.getChangeForValue(registry, "38");
        Assertions.assertThat(change).isEqualTo("1 1 1 1 1");

        String state = registryService.showCashierRegistryState(registry);
        Assertions.assertThat(state).isEqualTo("$30 0 1 2 3 4");
    }

    @Test
    void testChangeForValidValueButInsufficient() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        String state = registryService.getChangeForValue(registry, "69");
        Assertions.assertThat(state).isEqualTo(CashierRegistryServiceImpl.NO_CHANGE);
    }

    @Test
    void testChangeForInvalidValue() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        Assertions.assertThatIllegalArgumentException().isThrownBy(
                () -> registryService.getChangeForValue(registry, "%ABC*")
        );
    }

    @Test
    void testChangeForNegativeValue() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        Assertions.assertThatIllegalArgumentException().isThrownBy(
                () -> registryService.getChangeForValue(registry, "-38")
        );
    }

    @Test
    void testChangeForZeroValue() {
        CashierRegistry registry = registryRepo.findById(1L).orElse(null);
        String change = registryService.getChangeForValue(registry, "0");
        Assertions.assertThat(change).isEqualTo("0 0 0 0 0");

        String state = registryService.showCashierRegistryState(registry);
        Assertions.assertThat(state).isEqualTo("$68 1 2 3 4 5");
    }
}
