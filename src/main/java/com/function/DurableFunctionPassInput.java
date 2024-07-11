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
import com.model.HandlerResult;
import com.model.ResponseDTO;

public class DurableFunctionPassInput {
    

      // Orchestration with POJO input
    @FunctionName("StartOrchestrationPOJO")
    public HttpResponseMessage startOrchestrationPOJO(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<ClientDTO>> request,
        @DurableClientInput(name = "durableContext") DurableClientContext durableContext,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        

        // // in case of raw string reading
        // final String rawRequest = request.getBody().orElse(null);

        // ObjectMapper mapper = new ObjectMapper(); 
        // ClientDTO clientDTO = null;
        
        // try {
        //     clientDTO = mapper.readValue(rawRequest, ClientDTO.class);
        // } catch (Exception e) {
        //     context.getLogger().warning("Failed to deserialize input: " + e.getMessage());
        //     return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Failed to deserialize input").build();
        // }

        ClientDTO clientDTO = request.getBody().get();

        DurableTaskClient client = durableContext.getClient();
        String instanceId = client.scheduleNewOrchestrationInstance("AccountOrchestration", clientDTO);
        context.getLogger().info("Created new Java orchestration with instance ID = " + instanceId);
        return durableContext.createCheckStatusResponse(request, instanceId);
    }


    @FunctionName("AccountOrchestration") //this is orchestrator function
    public ResponseDTO accountOrchestrator(
        @DurableOrchestrationTrigger(name = "ctx") TaskOrchestrationContext ctx) {
        ClientDTO person = ctx.getInput(ClientDTO.class);

        List<String> handlers = new ArrayList<>();
        handlers.add("PersonalInfo");
        handlers.add("Address");
        handlers.add("KycInfo");

        List<String> result = new ArrayList<>();    

        for(String s: handlers){
            HandlerResult r = ctx.callActivity(s, person, HandlerResult.class).await();
            result.add(r.getResult());
            person = r.getClientDTO();
        }


        // HandlerResult r1 = ctx.callActivity("PersonalInfo", person, HandlerResult.class).await(); //handler1
        // HandlerResult r2 = ctx.callActivity("Address", r1.getClientDTO(),HandlerResult.class).await(); //handler2
        // HandlerResult r3 = ctx.callActivity("KycInfo", r2.getClientDTO(),HandlerResult.class).await(); //handler3
    

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setClientDTO(person);
        responseDTO.setResult(result);

        return responseDTO;
    }

    @FunctionName("PersonalInfo")
    public HandlerResult personNameInfo(
            @DurableActivityTrigger(name = "name") ClientDTO clientDto,
            final ExecutionContext context) {

        String result  = null;
                
        if(clientDto.getName().contains("Abhi")){
            clientDto.setName(clientDto.getName() + " processed done done");  //some processing done here
            result = "Success";
        }else{
            result = "Skipped";
        }         
        return new HandlerResult(result, clientDto);
    }

    @FunctionName("Address")
    public HandlerResult addressInfo(
            @DurableActivityTrigger(name = "name") ClientDTO clientDto,
            final ExecutionContext context) {

        String result = null;
                
        if(clientDto.getAddress().contains("India")){
            clientDto.setAddress(clientDto.getAddress() + " processed done done");    
            result = "Success";        
        }else{
            result = "Skipped";
        }

        return new HandlerResult(result, clientDto);
    }

    @FunctionName("KycInfo")
    public HandlerResult kycInfo(
            @DurableActivityTrigger(name = "name") ClientDTO clientDto,
            final ExecutionContext context) {

        String result = null;        
        if(clientDto.getKycInfo().contains("done")){
            clientDto.setKycInfo(clientDto.getKycInfo() + " processed done done");            
            result = "Success";
        }else{
            result = "Skipped";
        }
        
        return new HandlerResult(result, clientDto);
    }
}
