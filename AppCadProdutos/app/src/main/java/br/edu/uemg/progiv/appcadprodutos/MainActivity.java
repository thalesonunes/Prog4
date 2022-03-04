package br.edu.uemg.progiv.appcadprodutos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import br.edu.uemg.progiv.appcadprodutos.DAO.ProdutosDAO;
import br.edu.uemg.progiv.appcadprodutos.Model.ProdutosModel;

public class MainActivity extends AppCompatActivity {

    //Declarar as variáveis de CAST/PARSE
    ListView listView;
    Button btnCadastrar;
    ProdutosDAO produtosDAO;
    ArrayList<ProdutosModel> listaProdutos;
    ArrayAdapter adapter;
    ProdutosModel produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listaProdutos);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Produtos.class);
                startActivity(intent);
            }
        });

        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                produto = (ProdutosModel) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, Produtos.class);
                intent.putExtra("selectProduto", produto);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                produto = (ProdutosModel) parent.getItemAtPosition(position);
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem menuItem = menu.add("Deletar produto");
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                produtosDAO = new ProdutosDAO(MainActivity.this);
                produtosDAO.deletarProduto(produto);
                produtosDAO.close();
                carregarListaProdutos();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarListaProdutos();
    }

    //Método para buscar os dados no BD:
    public void carregarListaProdutos(){
        produtosDAO = new ProdutosDAO(MainActivity.this);
        listaProdutos = produtosDAO.listaProdutos();
        produtosDAO.close();
        if(listaProdutos != null){
            adapter = new ArrayAdapter<ProdutosModel>(
                    MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    listaProdutos
            );
            listView.setAdapter(adapter);
        }
    }

}