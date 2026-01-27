package id.task.gtech.controller;

import id.task.gtech.dto.AccountDto;
import id.task.gtech.dto.ResponseDto;
import id.task.gtech.dto.TransferDto;
import id.task.gtech.model.Account;
import id.task.gtech.repository.AccountRepository;
import id.task.gtech.repository.TransferRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerTest {

    private static final String DB_ACCOUNT = "1001";
    private static final String CR_ACCOUNT = "1002";
    private static final BigDecimal DB_BALANCE = BigDecimal.valueOf(1_000_000);
    private static final BigDecimal CR_BALANCE = BigDecimal.valueOf(100_000);
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(200_000);
    private static final BigDecimal LARGE_AMOUNT = BigDecimal.valueOf(2_000_000);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void createAccount() {
        Account dbAccount = new Account();
        dbAccount.setAccountNo(DB_ACCOUNT);
        dbAccount.setBalance(DB_BALANCE);
        accountRepository.save(dbAccount);

        Account crAccount = new Account();
        crAccount.setAccountNo(CR_ACCOUNT);
        crAccount.setBalance(CR_BALANCE);
        accountRepository.save(crAccount);
    }

    @AfterEach
    void deleteAccount() {
        accountRepository.deleteAll();
        transferRepository.deleteAll();
    }

    /**
     * a. Saldo dapat bertambah dan berkurang.
     * b. Setiap transaksi yang berhasil, harus mengurangi saldo.
     * c. Setiap transaksi top-up atau refund akan menambah saldo
     */
    @Test
    void transfer_OK() throws Exception {
        String code = UUID.randomUUID().toString(); // Assumption, code is UUID + type transfer (top-up / refund)
        TransferDto transferDto = new TransferDto();
        transferDto.setDebitAccount(DB_ACCOUNT);
        transferDto.setCreditAccount(CR_ACCOUNT);
        transferDto.setAmount(AMOUNT);
        transferDto.setTransactionCode(code);
        String request = objectMapper.writeValueAsString(transferDto);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .contentType(MediaType.APPLICATION_JSON).content(request)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        ResponseDto<String> responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<ResponseDto<String>>() {
        });
        Assertions.assertNull(responseDto.getError());

        // 1. Make sure Balance is change for DB Account
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", DB_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseDto<AccountDto> responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        AccountDto data = responseDtoAcc.getData();
        Assertions.assertEquals(DB_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, DB_BALANCE.subtract(AMOUNT).compareTo(data.getBalance()));

        // 2. Make sure Balance is change for CR Account
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", CR_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        contentAsString = mvcResult.getResponse().getContentAsString();
        responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        data = responseDtoAcc.getData();
        Assertions.assertEquals(CR_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, CR_BALANCE.add(AMOUNT).compareTo(data.getBalance()));
    }

    @Test
    void transfer_DbAccountNotFound() throws Exception {
        String code = UUID.randomUUID().toString();
        TransferDto transferDto = new TransferDto();
        transferDto.setDebitAccount("Salah");
        transferDto.setCreditAccount(CR_ACCOUNT);
        transferDto.setAmount(AMOUNT);
        transferDto.setTransactionCode(code);
        String request = objectMapper.writeValueAsString(transferDto);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .contentType(MediaType.APPLICATION_JSON).content(request)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), status);
        ResponseDto<String> responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<ResponseDto<String>>() {
        });
        Assertions.assertNotNull(responseDto.getError());

        // Check if balance DB Account still same
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", DB_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseDto<AccountDto> responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        AccountDto data = responseDtoAcc.getData();
        Assertions.assertEquals(DB_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, DB_BALANCE.compareTo(data.getBalance()));

        // Check if balance CR Account still same
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", CR_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        contentAsString = mvcResult.getResponse().getContentAsString();
        responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        data = responseDtoAcc.getData();
        Assertions.assertEquals(CR_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, CR_BALANCE.compareTo(data.getBalance()));
    }

    @Test
    void transfer_CrAccountNotFound() throws Exception {
        String code = UUID.randomUUID().toString();
        TransferDto transferDto = new TransferDto();
        transferDto.setDebitAccount(DB_ACCOUNT);
        transferDto.setCreditAccount("Salah");
        transferDto.setAmount(AMOUNT);
        transferDto.setTransactionCode(code);
        String request = objectMapper.writeValueAsString(transferDto);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .contentType(MediaType.APPLICATION_JSON).content(request)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), status);
        ResponseDto<String> responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<ResponseDto<String>>() {
        });
        Assertions.assertNotNull(responseDto.getError());

        // Check if balance DB Account still same
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", DB_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseDto<AccountDto> responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        AccountDto data = responseDtoAcc.getData();
        Assertions.assertEquals(DB_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, DB_BALANCE.compareTo(data.getBalance()));

        // Check if balance CR Account still same
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", CR_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        contentAsString = mvcResult.getResponse().getContentAsString();
        responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        data = responseDtoAcc.getData();
        Assertions.assertEquals(CR_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, CR_BALANCE.compareTo(data.getBalance()));
    }

    /**
     * e. Saldo tidak bisa minus (-)
     */
    @Test
    void transfer_NotMinus() throws Exception {
        String code = UUID.randomUUID().toString();
        TransferDto transferDto = new TransferDto();
        transferDto.setDebitAccount(DB_ACCOUNT);
        transferDto.setCreditAccount(CR_ACCOUNT);
        transferDto.setAmount(LARGE_AMOUNT);
        transferDto.setTransactionCode(code);
        String request = objectMapper.writeValueAsString(transferDto);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .contentType(MediaType.APPLICATION_JSON).content(request)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), status); // not minus error
        ResponseDto<String> responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<ResponseDto<String>>() {
        });
        Assertions.assertNotNull(responseDto.getError());

        // 1. Check if balance DB Account still same
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", DB_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseDto<AccountDto> responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        AccountDto data = responseDtoAcc.getData();
        Assertions.assertEquals(DB_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, DB_BALANCE.compareTo(data.getBalance()));

        // 2. Check if balance CR Account still same
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", CR_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        contentAsString = mvcResult.getResponse().getContentAsString();
        responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        data = responseDtoAcc.getData();
        Assertions.assertEquals(CR_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, CR_BALANCE.compareTo(data.getBalance()));
    }

    /**
     * d. Implemen logic untuk memastikan saldo tidak terpotong/bertambah lebih dari 1 kali jika ada pemanggilan duplikat
     */
    @Test
    void transfer_NoDuplicate() throws Exception {
        String code = UUID.randomUUID().toString();
        TransferDto transferDto = new TransferDto();
        transferDto.setDebitAccount(DB_ACCOUNT);
        transferDto.setCreditAccount(CR_ACCOUNT);
        transferDto.setAmount(AMOUNT);
        transferDto.setTransactionCode(code);
        String request = objectMapper.writeValueAsString(transferDto);

        for (int i = 0; i < 2; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                    .contentType(MediaType.APPLICATION_JSON).content(request)).andReturn();
        }

        // 1. Check if balance DB Account still same
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", DB_ACCOUNT))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseDto<AccountDto> responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        AccountDto data = responseDtoAcc.getData();
        Assertions.assertEquals(DB_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, DB_BALANCE.subtract(AMOUNT).compareTo(data.getBalance()));

        // 2. Check if balance CR Account still same
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", CR_ACCOUNT))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        contentAsString = mvcResult.getResponse().getContentAsString();
        responseDtoAcc = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        data = responseDtoAcc.getData();
        Assertions.assertEquals(CR_ACCOUNT, data.getAccountNo());
        Assertions.assertEquals(0, CR_BALANCE.add(AMOUNT).compareTo(data.getBalance()));
    }

}
