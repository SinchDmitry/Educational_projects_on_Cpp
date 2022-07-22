package edu.school21.chat.app;

import com.zaxxer.hikari.HikariDataSource;
import edu.school21.chat.models.Chatroom;
import edu.school21.chat.models.Message;
import edu.school21.chat.models.User;
import edu.school21.chat.repositories.MessagesRepositoryJdbcImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class Program {
    public static HikariDataSource hikariInit () {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
//        ds.setUsername("postgres");
//        ds.setPassword("postgres");
//        ds.addDataSourceProperty( "cachePrepStmts" , "true" );
//        ds.addDataSourceProperty(  "prepStmtCacheSize" , "250" );
//        ds.addDataSourceProperty(  "prepStmtCacheSqlLimit" , "2048" );
        return ds;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        HikariDataSource source = hikariInit();
        MessagesRepositoryJdbcImpl msgRep = new MessagesRepositoryJdbcImpl(source);
        System.out.println("Create user & room with ID = null");
        User somebody = new User(null, "shreck", "boloto", new ArrayList<>(), new ArrayList<>());
        Chatroom boloto = new Chatroom(null, "boloto", somebody, new ArrayList<>());
        System.out.println("Trying to save new message");
        Message msg = new Message(null, somebody, boloto, "My uje priehali?", LocalDateTime.now());

        try {
            msgRep.save(msg);
        } catch (MessagesRepositoryJdbcImpl.NotSavedSubEntityException e) {
            System.out.println(e.getMessage());
            System.out.println();
        }

        System.out.println("Set author ID, room ID & trying to save new message");
        somebody.setId(100L);
        boloto.setId(101L);

        try {
            msgRep.save(msg);
        } catch (MessagesRepositoryJdbcImpl.NotSavedSubEntityException e) {
            System.out.println(e.getMessage());
            System.out.println();
        }

        System.out.println("Correct author ID, room ID & trying to save new message");

        somebody.setId(6L);
        boloto.setId(6L);

        try {
            msgRep.save(msg);
        } catch (MessagesRepositoryJdbcImpl.NotSavedSubEntityException e) {
            System.out.println(e.getMessage());
            System.out.println();
        }
        System.out.println(msg.getId());

        System.out.println("Enter a message ID : ");
        if (input.hasNextLong()) {
            Long id = input.nextLong();
            Optional<Message> IdMessage = msgRep.findById(id);
            if (IdMessage.isPresent()) {
                String tmp = IdMessage.get().toString();
                tmp = tmp.substring(0, tmp.length() - 1)
                        .replace("Message{", "Message : {\n\t")
                        .replace(" author=User", "\n\tauthor=")
                        .replace(" chatroom=Chatroom", "\n\troom=")
                        .replace(" data=", "\n\tdata=")
                        .replace(" message=", "\n\ttext=") + "\n}";
                System.out.println(tmp);
            }
        } else {
            System.err.println("Error : wrong type of input data");
            System.exit(-1);
        }

    }
}
