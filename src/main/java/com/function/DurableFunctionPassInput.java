package com.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.durabletask.DurableTaskClient;
import com.microsoft.durabletask.Task;
import com.microsoft.durabletask.TaskOrchestrationContext;
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger;
import com.microsoft.durabletask.azurefunctions.DurableClientContext;
import com.microsoft.durabletask.azurefunctions.DurableClientInput;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;
import com.model.ClientDTO;

public class DurableFunctionPassInput {
    

      // Orchestration with POJO input
    @FunctionName("StartOrchestrationPOJO")
    public HttpResponseMessage startOrchestrationPOJO(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
        @DurableClientInput(name = "durableContext") DurableClientContext durableContext,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        
        final String rawRequest = request.getBody().orElse(null);

        ObjectMapper mapper = new ObjectMapper(); 
        ClientDTO clientDTO = null;
        
        try {
            clientDTO = mapper.readValue(rawRequest, ClientDTO.class);
        } catch (Exception e) {
            context.getLogger().warning("Failed to deserialize input: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Failed to deserialize input").build();
        }

        DurableTaskClient client = durableContext.getClient();
        String instanceId = client.scheduleNewOrchestrationInstance("AccountOrchestration", clientDTO);
        context.getLogger().info("Created new Java orchestration with instance ID = " + instanceId);
        return durableContext.createCheckStatusResponse(request, instanceId);
    }


    @FunctionName("AccountOrchestration") //this is orchestrator function
    public ClientDTO accountOrchestrator(
        @DurableOrchestrationTrigger(name = "ctx") TaskOrchestrationContext ctx) {
        ClientDTO person = ctx.getInput(ClientDTO.class);

        person = ctx.callActivity("PersonalInfo", person, ClientDTO.class).await(); //handler1
        person = ctx.callActivity("Address", person,ClientDTO.class).await(); //handler2
        person = ctx.callActivity("KycInfo", person,ClientDTO.class).await(); //handler3
        
        return person;
    }

    @FunctionName("PersonalInfo")
    public ClientDTO personNameInfo(
            @DurableActivityTrigger(name = "name") ClientDTO clientDto,
            final ExecutionContext context) {
        clientDto.setName(clientDto.getName() + " processed done");            
        context.getLogger().info("Capitalizing: " + clientDto);
        
        return clientDto;
    }

    @FunctionName("Address")
    public ClientDTO addressInfo(
            @DurableActivityTrigger(name = "name") ClientDTO clientDto,
            final ExecutionContext context) {
        clientDto.setAddress(clientDto.getAddress() + " processed done");            
        context.getLogger().info("Capitalizing: " + clientDto);
        
        return clientDto;
    }

    @FunctionName("KycInfo")
    public ClientDTO kycInfo(
            @DurableActivityTrigger(name = "name") ClientDTO clientDto,
            final ExecutionContext context) {
        clientDto.setKycInfo(clientDto.getKycInfo() + " processed done");            
        context.getLogger().info("Capitalizing: " + clientDto);
        
        return clientDto;
    }
}
