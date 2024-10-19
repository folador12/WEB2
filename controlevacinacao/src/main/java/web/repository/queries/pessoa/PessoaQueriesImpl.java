package web.repository.queries.pessoa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import web.filter.PessoaFilter;
import web.model.Pessoa;
import web.model.Status;
import web.repository.pagination.PaginacaoUtil;
import web.repository.queries.vacina.VacinaQueriesImpl;

import java.util.ArrayList;
import java.util.List;

public class PessoaQueriesImpl implements PessoaQueries {
    @PersistenceContext
    private EntityManager em;

    private final Logger logger = LoggerFactory.getLogger(VacinaQueriesImpl.class);

    @Override
    public Page<Pessoa> pesquisar(PessoaFilter filtro, Pageable pageable) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Pessoa> criteriaQuery = builder.createQuery(Pessoa.class);
        Root<Pessoa> pessoaRoot = criteriaQuery.from(Pessoa.class);
        TypedQuery<Pessoa> typedQuery;
        List<Predicate> predicateList = new ArrayList<>();
        List<Predicate> predicateListTotal = new ArrayList<>();
        Predicate[] predicates;
        Predicate[] totalPredicates;

        if(filtro.getCodigo() != null ) {
            predicateList.add(builder.equal(pessoaRoot.get("codigo"), filtro.getCodigo()));
        }

        if(StringUtils.hasText(filtro.getNome())) {
            predicateList.add(builder.like(builder.lower(pessoaRoot.get("nome")), "%" + filtro.getNome().toLowerCase() + "%"));
        }

        if(StringUtils.hasText(filtro.getCpf())) {
            predicateList.add(builder.like(builder.lower(pessoaRoot.get("cpf")), "%" + filtro.getCpf().toLowerCase() + "%"));
        }

        if(StringUtils.hasText(filtro.getProfissao())) {
            predicateList.add(builder.like(builder.lower(pessoaRoot.get("profissao")), "%" + filtro.getProfissao().toLowerCase() + "%"));
        }

        if(filtro.getDataInicio() != null && filtro.getDataFim() != null) {
            predicateList.add(builder.between(pessoaRoot.get("data_nascimento"), filtro.getDataInicio(), filtro.getDataFim()));
        }else if(filtro.getDataInicio() != null) {
            predicateList.add(builder.greaterThanOrEqualTo(pessoaRoot.get("data_nascimento"), filtro.getDataInicio()));
        }else if(filtro.getDataFim() != null) {
            predicateList.add(builder.lessThanOrEqualTo(pessoaRoot.get("data_nascimento"), filtro.getDataFim()));
        }

        predicateList.add(builder.equal(pessoaRoot.get("status"), Status.ATIVO));

        predicates = new Predicate[predicateList.size()];
        predicateList.toArray(predicates);
        criteriaQuery.select(pessoaRoot).where(predicates);
        PaginacaoUtil.prepararOrdem(pessoaRoot, criteriaQuery, builder, pageable);
        typedQuery = em.createQuery(criteriaQuery);
        PaginacaoUtil.prepararIntervalo(typedQuery, pageable);
        typedQuery.setHint("hibernate.query.passDistinctThrough", false);
        List<Pessoa> pessoas = typedQuery.getResultList();
        logger.info("Calculando o total de registros que o filtro retornará.");
        CriteriaQuery<Long> criteriaQueryTotal = builder.createQuery(Long.class);

        if(filtro.getCodigo() != null ) {
            predicateListTotal.add(builder.equal(pessoaRoot.get("codigo"), filtro.getCodigo()));
        }

        if(StringUtils.hasText(filtro.getNome())) {
            predicateListTotal.add(builder.like(builder.lower(pessoaRoot.get("nome")), "%" + filtro.getNome().toLowerCase() + "%"));
        }

        if(StringUtils.hasText(filtro.getCpf())) {
            predicateListTotal.add(builder.like(builder.lower(pessoaRoot.get("cpf")), "%" + filtro.getCpf().toLowerCase() + "%"));
        }

        if(StringUtils.hasText(filtro.getProfissao())) {
            predicateListTotal.add(builder.like(builder.lower(pessoaRoot.get("profissao")), "%" + filtro.getProfissao().toLowerCase() + "%"));
        }

        if(filtro.getDataInicio() != null && filtro.getDataFim() != null) {
            predicateListTotal.add(builder.between(pessoaRoot.get("data_nascimento"), filtro.getDataInicio(), filtro.getDataFim()));
        }else if(filtro.getDataInicio() != null) {
            predicateListTotal.add(builder.greaterThanOrEqualTo(pessoaRoot.get("data_nascimento"), filtro.getDataInicio()));
        }else if(filtro.getDataFim() != null) {
            predicateListTotal.add(builder.lessThanOrEqualTo(pessoaRoot.get("data_nascimento"), filtro.getDataFim()));
        }

        predicateListTotal.add(builder.equal(pessoaRoot.get("status"), Status.ATIVO));
        totalPredicates = new Predicate[predicateListTotal.size()];
        predicateListTotal.toArray(totalPredicates);
        criteriaQueryTotal.where(totalPredicates);
        TypedQuery<Long> typeQueryTotal = em.createQuery(criteriaQueryTotal);
        long totalPessoas = typeQueryTotal.getSingleResult();
        logger.info("O filtro retornará {} registros.", totalPessoas);
        return new PageImpl<>(pessoas, pageable, totalPessoas);

    }
}
