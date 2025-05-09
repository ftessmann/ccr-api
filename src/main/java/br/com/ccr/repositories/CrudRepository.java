package br.com.ccr.repositories;

import java.util.List;
import java.util.Optional;

// <T> implementa tipo T generico
public interface CrudRepository<T> {
    T salvar(T object);

    T adicionar(T object);

    void atualizar(int id, T object);

    void remover(T object);
    void remover(int id);

    void delete(T object);
    void deleteById(int id);

    List<T> listarTodos();
    List<T> listar();
    Optional<T> buscarPorId(int id);
}
