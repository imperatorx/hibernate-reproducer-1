package com.example.reproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.*;

@SpringBootApplication
public class ReproducerApplication implements CommandLineRunner {

    @Autowired
    private EntityManager em;

    public static void main(String[] args) {
        SpringApplication.run(ReproducerApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        var cb = em.getCriteriaBuilder();

        var cq = cb.createQuery(MainEntity.class);
        var root = cq.from(MainEntity.class);

        cq.groupBy(
                root.get("linked1"),
                root.get("linked2"),
                root.get("category")
        );

        cq.select(cb.construct(MainEntity.class,
                root.get("linked1"),
                root.get("linked2"),
                root.get("category"),
                cb.sum(root.get("counter"))
        ));

        /*

        If you change the field order here and in the constructor too, it works !?

        cq.select(cb.construct(MainEntity.class,
                root.get("category"),
                cb.sum(root.get("counter")),
                root.get("linked1"),
                root.get("linked2")

        ));
         */

        em.createQuery(cq)
                .getResultList();
    }

    @Entity
    public static class MainEntity {

        public MainEntity() {
        }

        // Change to MainEntity(String category, int counter, LinkedEntity linked1, LinkedEntity linked2) and it works !?
        public MainEntity(LinkedEntity linked1, LinkedEntity linked2, String category, long counter) {
            this.category = category;
            this.counter = counter;
            this.linked1 = linked1;
            this.linked2 = linked2;
        }

        private Integer id;

        private String category;

        private long counter;

        private LinkedEntity linked1;

        private LinkedEntity linked2;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public long getCounter() {
            return counter;
        }

        public void setCounter(long counter) {
            this.counter = counter;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        @ManyToOne(fetch = FetchType.LAZY, targetEntity = LinkedEntity.class, optional = false)
        @JoinColumn(name = "linked_1_id", nullable = false)
        public LinkedEntity getLinked1() {
            return linked1;
        }

        public void setLinked1(LinkedEntity linked1) {
            this.linked1 = linked1;
        }

        @ManyToOne(fetch = FetchType.LAZY, targetEntity = LinkedEntity.class, optional = false)
        @JoinColumn(name = "linked_2_id", nullable = false)
        public LinkedEntity getLinked2() {
            return linked2;
        }

        public void setLinked2(LinkedEntity linked2) {
            this.linked2 = linked2;
        }
    }

    @Entity
    public static class LinkedEntity {
        private Integer id;

        private String name;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
