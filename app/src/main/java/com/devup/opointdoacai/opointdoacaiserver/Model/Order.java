package com.devup.opointdoacai.opointdoacaiserver.Model;

public class Order {

    private String Quantidade;
    private String Complementos;
    private String Preco;

    public Order() {
    }

    public Order(String quantidade, String complementos, String preco) {
        Quantidade = quantidade;
        Complementos = complementos;
        Preco = preco;
    }

    public String getQuantidade() {
        return Quantidade;
    }

    public void setQuantidade(String quantidade) {
        Quantidade = quantidade;
    }

    public String getComplementos() {
        return Complementos;
    }

    public void setComplementos(String complementos) {
        Complementos = complementos;
    }

    public String getPreco() {
        return Preco;
    }

    public void setPreco(String preco) {
        Preco = preco;
    }
}
