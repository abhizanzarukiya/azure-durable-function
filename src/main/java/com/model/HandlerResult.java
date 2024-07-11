package com.model;

import java.io.Serializable;

public class HandlerResult implements Serializable
{
    private String result;

    private ClientDTO clientDTO;

    public HandlerResult() {
    }

    public HandlerResult(String result, ClientDTO clientDTO) {
        this.result = result;
        this.clientDTO = clientDTO;
    }


    public ClientDTO getClientDTO() {
        return clientDTO;
    }

    public void setClientDTO(ClientDTO clientDTO) {
        this.clientDTO = clientDTO;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
