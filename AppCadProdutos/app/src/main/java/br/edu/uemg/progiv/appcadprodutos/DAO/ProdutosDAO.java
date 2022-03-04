package br.edu.uemg.progiv.appcadprodutos.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import br.edu.uemg.progiv.appcadprodutos.Model.ProdutosModel;
import br.edu.uemg.progiv.appcadprodutos.Produtos;

public class ProdutosDAO extends SQLiteOpenHelper {

    private static final String DATABASE = "cadprodutos"; //nome do banco de dados
    private static final int VERSION = 1; //versão do banco de dados

    public String msmErro = "";

    public ProdutosDAO(Context context){
        super(context, DATABASE, null, VERSION);
        msmErro = "";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE produtos(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "nome TEXT NOT NULL, " +
                "descricao TEXT NOT NULL, " +
                "quantidade INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //verificar a versão do banco de dados, caso for a mesma
        // versão, não alterar:
        if(oldVersion != newVersion){
            String sql = "DROP TABLE IF EXISTS produtos";
            db.execSQL(sql);
        }
    }

    //CRUD:
    //Método de inserir produtos:
    public boolean salvarProduto(ProdutosModel produto){
        try {
            ContentValues values = new ContentValues();
            values.put("nome", produto.getNome());
            values.put("descricao", produto.getDescricao());
            values.put("quantidade", produto.getQuantidade());
            getWritableDatabase().insert("produtos", null, values);
            return true;
        }catch (Exception ex){
            msmErro = ex.getMessage();
            return false;
        }
    }

    //Metodo alterar produto
    public boolean alterarProduto(ProdutosModel produto){
        try{
            ContentValues values = new ContentValues();
            values.put("nome", produto.getNome());
            values.put("descricao", produto.getDescricao());
            values.put("quantidade", produto.getQuantidade());

            String[] args = {produto.getId().toString()};
            getWritableDatabase().update("produtos", values,"id=?", args);
            return true;
        }catch (Exception ex){
            msmErro = ex.getMessage();
            return false;
        }
    }

    //Metodo de deletar o produto:
    public boolean deletarProduto(ProdutosModel produto){
        try{
            String[] args = {produto.getId().toString()};
            getWritableDatabase().delete("produtos","id=?",args);
            return true;
        }catch (Exception ex){
            msmErro = ex.getMessage();
            return false;
        }
    }

    //Método de listar os produtos:
    public ArrayList<ProdutosModel> listaProdutos(){
        ArrayList<ProdutosModel> lstProdutos = new ArrayList<>();
        String[] colums = {"id", "nome", "descricao", "quantidade"};
        Cursor cursor = getWritableDatabase().query("produtos", colums, null, null, null, null, null);
        while(cursor.moveToNext()){
            ProdutosModel produtosModel = new ProdutosModel();
            produtosModel.setId(cursor.getLong(0));
            produtosModel.setNome(cursor.getString(1));
            produtosModel.setDescricao(cursor.getString(2));
            produtosModel.setQuantidade(cursor.getInt(3));
            lstProdutos.add(produtosModel);
        }
        return lstProdutos;
    }

}
