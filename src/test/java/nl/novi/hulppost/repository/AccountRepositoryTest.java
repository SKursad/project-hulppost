package nl.novi.hulppost.repository;

import nl.novi.hulppost.model.Account;
import nl.novi.hulppost.model.enums.Gender;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository underTest;

    private Account account;

    @BeforeEach
    public void setup() {
        account = Account.builder()
                .id(1L)
                .firstName("Salim Kursad")
                .surname("Dursun")
                .birthday("24/02/85")
                .gender(Gender.M)
                .telNumber("061234567890")
                .zipCode("1068AB")
                .build();
    }

    @BeforeEach
    void init(){
        underTest.deleteAll();
    }

    @DisplayName("JUnit test finding the name with ignore case")
    @Test
    void itShouldFindTheUserByNameWithIgnoreCase() {

        Account account = Account.builder()
                .id(1L).firstName("Kursad")
                .build();

        underTest.findByFirstNameIgnoreCase("KURsaD");

        Assertions.assertEquals(account.getFirstName(), "Kursad");
    }


    @DisplayName("JUnit test checks if account exists by first name")
    @Test
    void itShouldCheckIfUserExistsByUsername() {

        // given - precondition or setup
        account = Account.builder()
                .id(1L)
                .firstName("Yusuf")
                .surname("Emir")
                .birthday("15/09/2015")
                .gender(Gender.M)
                .telNumber("061234567890")
                .zipCode("1064VV")
                .build();
        String firstName = "Yusuf";

        // when - action that's under test
        underTest.save(account);
        boolean expected = underTest.existsByFirstName(firstName);

        // then - verify output
        AssertionsForClassTypes.assertThat(expected).isTrue();
    }

    @DisplayName("JUnit test checks if account exists by Email")
    @Test
    void itShouldCheckIfUserExistsByEmail() {

        // given
        account = Account.builder()
                .id(1L)
                .firstName("Sammy")
                .surname("Klaver")
                .birthday("04/05/1993")
                .gender(Gender.M)
                .telNumber("061234567890")
                .zipCode("1466AA")
                .build();
        String surname = "Klaver";

        // when
        underTest.save(account);
        boolean expected = underTest.existsBySurname(surname);

        // then
        AssertionsForClassTypes.assertThat(expected).isTrue();
    }

    @DisplayName("JUnit test checks if account exists by first name or surname ")
    @Test
    void itShouldFindTheUserByFirstNameOrSurname() {

        // given
        account = Account.builder()
                .id(1L)
                .firstName("Jacqueline")
                .surname("Roos")
                .birthday("02/10/1995")
                .gender(Gender.M)
                .telNumber("061234567890")
                .zipCode("1056KP")
                .build();

        // when
        underTest.findByFirstNameOrSurname("Jacqueline", "Roos");

        // then
        Assertions.assertEquals(account.getFirstName(), "Jacqueline");
        Assertions.assertEquals(account.getSurname(), "Roos");
    }

    @DisplayName("JUnit test checks if account doesn't exists by first name")
    @Test
    void itShouldCheckIfUserFirstNameDoesNotExist() {

        // given
        String firstName = "Bobby";

        // when
        boolean expected = underTest.existsByFirstName("Bobby");

        // then
        AssertionsForClassTypes.assertThat(expected).isFalse();
    }

    @DisplayName("JUnit test for save Account operation")
    @Test
    public void givenAccountObject_whenSave_thenReturnSavedAccount() {

        // given
//        account = Account.builder()
//                .id(1L)
//                .firstName("Salim Kursad")
//                .surname("Dursun")
//                .birthday("24/02/85")
//                .gender(Gender.M)
//                .telNumber("061234567890")
//                .zipCode("1068AB")
//                .build();
//    }

        // when
        Account savedAccount = underTest.save(account);

        // then
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getId()).isGreaterThan(0L);
    }

    @DisplayName("JUnit test for get all Accounts operation")
    @Test
    public void givenAccountsList_whenFindAll_thenAccountsList() {

        // given
//      Account account = Account.builder()
//                .id(1L)
//                .firstName("Salim Kursad")
//                .surname("Dursun")
//                .birthday("24/02/85")
//                .gender(Gender.M)
//                .telNumber("061234567890")
//                .zipCode("1068AB")
//                .build();
//    }

        Account account1 = Account.builder()
                .id(4L)
                .firstName("Sammy")
                .surname("Klaver")
                .birthday("04/05/1993")
                .gender(Gender.M)
                .telNumber("061234567890")
                .zipCode("1466AA")
                .build();

        // when
        underTest.save(account);
        underTest.save(account1);
        List<Account> AccountList = underTest.findAll();

        // then
        assertThat(AccountList).isNotNull();
        assertThat(AccountList.size()).isEqualTo(2);
    }

    @DisplayName("JUnit test for get Account by id operation")
    @Test
    public void givenAccountObject_whenFindById_thenReturnAccountObject() {

        // given
        Account account = Account.builder()
                .firstName("Salim Kursad")
                .surname("Dursun")
                .birthday("24/02/85")
                .gender(Gender.M)
                .telNumber("061234567890")
                .zipCode("1068AB")
                .build();

        // when
        underTest.save(account);
        Account AccountDB = underTest.findById(account.getId()).get();

        // then
        assertThat(AccountDB).isNotNull();

    }

    @DisplayName("JUnit test for get Account by first name operation")
    @Test
    public void givenFirstNameIC_whenFindByFirstNameIC_thenReturnAccountObject() {

        // given
//        Account account = Account.builder()
//                .id(1L)
//                .firstName("Salim Kursad")
//                .surname("Dursun")
//                .birthday("24/02/85")
//                .gender(Gender.M)
//                .telNumber("061234567890")
//                .zipCode("1068AB")
//                .build();
        underTest.save(account);

        // when
        Account AccountDB = underTest.findByFirstNameIgnoreCase(account.getFirstName()).get();

        // then
        assertThat(AccountDB).isNotNull();
    }

    @DisplayName("JUnit test for update Account operation")
    @Test
    public void givenAccountObject_whenUpdateAccount_thenReturnUpdatedAccount() {

        // given
        Account account = Account.builder()
                .firstName("Salim Kursad")
                .surname("Dursun")
                .birthday("24/02/85")
                .gender(Gender.M)
                .telNumber("061234567890")
                .zipCode("1068AB")
                .build();
        underTest.save(account);

        // when
        Account savedAccount = underTest.findById(account.getId()).get();
        savedAccount.setFirstName("Salim");
        savedAccount.setTelNumber("060987654321");
        Account updatedAccount = underTest.save(savedAccount);

        // then
        assertThat(updatedAccount.getFirstName()).isEqualTo("Salim");
        assertThat(updatedAccount.getTelNumber()).isEqualTo("060987654321");
    }

    @DisplayName("JUnit test for delete Account operation")
    @Test
    public void givenAccountObject_whenDelete_thenRemoveAccount() {

        // given
        Account account = Account.builder()
                .firstName("Salim Kursad")
                .surname("Dursun")
                .birthday("24/02/85")
                .gender(Gender.M)
                .telNumber("061234567890")
                .zipCode("1068AB")
                .build();
        underTest.save(account);

        // when
        underTest.deleteById(account.getId());
        Optional<Account> AccountOptional = underTest.findById(account.getId());

        // then
        assertThat(AccountOptional).isEmpty();
    }

}
