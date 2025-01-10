package com.vseeliu.balancecalc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vseeliu.balancecalc.entity.Transaction;
import com.vseeliu.balancecalc.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
public class TransactionControllerTest {
    private MockMvc mvc;

    @Autowired
    private TransactionController transactionController;

    @BeforeEach
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void postSyncTransactionWithShouldReturnOKTest() throws Exception {
        String msg =
            "{\n\"transactionId\" : \"3bf5127c-f3d9-4d7a-9c00-4b2ab32faa5a\",\n"
                + "  \"sourceAccountNumber\" : \"111111111111\",\n" + "  \"targetAccountNumber\" : \"222222222222\",\n"
                + "  \"amount\" : 1.11,\n" + "  \"timestamp\" : 1736438275\n" + "}";
        mvc.perform(MockMvcRequestBuilders.post("/v1/api/transactions").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(msg))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void postSyncTransactionWithParamWrongShouldReturnFailTest() throws Exception {
        String msg =
            "{\n\"transactionId\" : \"@@\",\n"
                + "  \"sourceAccountNumber\" : \"111111111111\",\n" + "  \"targetAccountNumber\" : \"222222222222\",\n"
                + "  \"amount\" : 1.11,\n" + "  \"timestamp\" : 1736438275\n" + "}";
        mvc.perform(MockMvcRequestBuilders.post("/v1/api/transactions").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(msg))
            .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void postASyncTransactionWithShouldReturnOKTest() throws Exception {
        String msg =
            "{\n\"transactionId\" : \"3bf5127c-f3d9-4d7a-9c00-4b2ab32faa5a\",\n"
                + "  \"sourceAccountNumber\" : \"111111111111\",\n" + "  \"targetAccountNumber\" : \"222222222222\",\n"
                + "  \"amount\" : 1.11,\n" + "  \"timestamp\" : 1736438275\n" + "}";
        mvc.perform(MockMvcRequestBuilders.post("/v1/api/transactions/transfer").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(msg))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void postASyncTransactionWithParamWrongShouldReturnFailTest() throws Exception {
        String msg =
            "{\n\"transactionId\" : \"@@@\",\n"
                + "  \"sourceAccountNumber\" : \"111111111111\",\n" + "  \"targetAccountNumber\" : \"222222222222\",\n"
                + "  \"amount\" : 1.11,\n" + "  \"timestamp\" : 1736438275\n" + "}";
        mvc.perform(MockMvcRequestBuilders.post("/v1/api/transactions/transfer").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(msg))
            .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void getSyncTransactionResultShouldReturnOKTest() throws Exception {
        String msg =
            "{\n\"transactionId\" : \"3bf5127c-f3d9-4d7a-9c00-4b2ab32faa5a\",\n"
                + "  \"sourceAccountNumber\" : \"111111111111\",\n" + "  \"targetAccountNumber\" : \"222222222222\",\n"
                + "  \"amount\" : 1.11,\n" + "  \"timestamp\" : 1736438275\n" + "}";
        mvc.perform(MockMvcRequestBuilders.get("/v1/api/transactions/3bf5127c-f3d9-4d7a-9c00-4b2ab32faa5a/result").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(""))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getSyncTransactionResultWithParamWrongShouldReturnFailTest() throws Exception {
        String msg =
            "{\n\"transactionId\" : \"3bf5127c-f3d9-4d7a-9c00-4b2ab32faa5a\",\n"
                + "  \"sourceAccountNumber\" : \"111111111111\",\n" + "  \"targetAccountNumber\" : \"222222222222\",\n"
                + "  \"amount\" : 1.11,\n" + "  \"timestamp\" : 1736438275\n" + "}";
        mvc.perform(MockMvcRequestBuilders.get("/v1/api/transactions/3bf5127c-f3d9-4d7a-9c00-4b2ab32faa51/result").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(""))
            .andExpect(MockMvcResultMatchers.status().is(500));
    }
}
