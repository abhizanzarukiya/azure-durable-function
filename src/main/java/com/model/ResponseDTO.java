package com.model;

import java.util.List;

public class ResponseDTO {
    
    List<String> result;

    private ClientDTO clientDTO;

    public ClientDTO getClientDTO() {
        return clientDTO;
    }

    public void setClientDTO(ClientDTO clientDTO) {
        this.clientDTO = clientDTO;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public String toString() {
        return "ClientDTO: " + clientDTO + ", Result: " + result;
    }

}
