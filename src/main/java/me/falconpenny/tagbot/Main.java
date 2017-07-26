package me.falconpenny.tagbot;

import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.util.stream.IntStream;

public class Main {
    @Getter
    private static final Main instance = new Main();
    @Getter
    private JDA jda;

    private void start(String token, AccountType type) throws LoginException, InterruptedException, RateLimitedException {
        jda = new JDABuilder(type).setToken(token).buildBlocking();

        Logging.log.info("Registering listener!");
        jda.addEventListener(new TagLogger());
        Logging.log.info("Start done.");
    }

    static String charVerify(String seq, int chars) {
        if (seq.length() >= chars) {
            return seq;
        }
        return seq + IntStream.rangeClosed(seq.length(), chars).mapToObj(i -> " ");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            Logging.log.severe("Syntax: java -jar <jar> TOKEN");
            System.exit(1);
            return;
        }
        instance.start(args[0].replace("\"", ""), args.length >= 2 ? (args[1].equals("0") ? AccountType.CLIENT : AccountType.BOT) : AccountType.CLIENT);
    }

    private Main() {}
}
