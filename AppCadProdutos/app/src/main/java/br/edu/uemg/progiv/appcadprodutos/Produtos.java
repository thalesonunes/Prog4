package br.edu.uemg.progiv.appcadprodutos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.edu.uemg.progiv.appcadprodutos.DAO.ProdutosDAO;
import br.edu.uemg.progiv.appcadprodutos.Model.ProdutosModel;

public class Produtos extends AppCompatActivity {
    EditText txtNomeProduto;
    EditText txtDescricaoProduto;
    EditText txtQuantidadeProduto;
    Button btnModificar;
    ProdutosModel editarProdutos;
    ProdutosModel produto;
    ProdutosDAO produtosDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        produto = new ProdutosModel();
        produtosDAO = new ProdutosDAO(Produtos.this);

        Intent intent = getIntent();
        //Criando um alias do Main para Produtos
        editarProdutos = (ProdutosModel) intent.getSerializableExtra("selectProduto");

        txtNomeProduto       = (EditText) findViewById(R.id.nomeProduto);
        txtDescricaoProduto  = (EditText) findViewById(R.id.descricaoProduto);
        txtQuantidadeProduto = (EditText) findViewById(R.id.quantidadeProduto);
        btnModificar         = (Button)   findViewById(R.id.btnModificar);

        if(editarProdutos != null){
            btnModificar.setText("Modificar");
            txtNomeProduto.setText(editarProdutos.getNome());
            txtDescricaoProduto.setText(editarProdutos.getDescricao());
            txtQuantidadeProduto.setText(String.valueOf(editarProdutos.getQuantidade()));
            produto.setId(editarProdutos.getId());
        }else{
            btnModificar.setText("Cadastrar");
        }

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                produto.setNome(txtNomeProduto.getText().toString());
                produto.setDescricao(txtDescricaoProduto.getText().toString());
                produto.setQuantidade(Integer.parseInt(txtQuantidadeProduto.getText().toString()));
                if(btnModificar
                        .getText()
                        .toString()
                        .toUpperCase()
                        .equals("CADASTRAR")
                ){
                    //insert
                    produtosDAO.salvarProduto(produto);
                }else{
                    //update
                    produtosDAO.alterarProduto(produto);
                }
                produtosDAO.close();
                finish();
            }
        });

    }
}
