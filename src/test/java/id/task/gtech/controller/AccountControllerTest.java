package id.task.gtech.controller;

import id.task.gtech.dto.AccountDto;
import id.task.gtech.dto.ResponseDto;
import id.task.gtech.model.Account;
import id.task.gtech.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerTest {

    private static final String DB_ACCOUNT = "1001";

    @Autowired
    private AccountRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void createAccount() {
        Account dbAccount = new Account();
        dbAccount.setAccountNo(DB_ACCOUNT);
        dbAccount.setBalance(BigDecimal.valueOf(1_000_000));
        repository.save(dbAccount);
    }

    @AfterEach
    void deleteAccount() {
        repository.deleteById(DB_ACCOUNT);
    }

    @Test
    void get_OK() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", DB_ACCOUNT))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.OK.value(), status);
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseDto<AccountDto> responseDto = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        AccountDto data = responseDto.getData();
        Assertions.assertEquals(DB_ACCOUNT, data.getAccountNo());
    }

    @Test
    void get_NotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/account/{accountNo}", "SALAH"))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), status);
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseDto<AccountDto> responseDto = objectMapper.readValue(contentAsString, new TypeReference<ResponseDto<AccountDto>>() {
        });
        Assertions.assertNull(responseDto.getData());
        Assertions.assertNotNull(responseDto.getError());
    }

}
