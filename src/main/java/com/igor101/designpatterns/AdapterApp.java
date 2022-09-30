package com.igor101.designpatterns;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*
Adapter pattern (sometimes also called Wrapper)
allows existing interface/class to be used as another interface or class.

It has two major usages:
* when we want to reuse current code, but the client requires another interface
* when we want to hide the complexity of existing class/interface behind something simpler

*/
public class AdapterApp {
    public static void main(String[] args) {
        //reuse current code with different interface
        var frameworkSpecificRepository = new FrameworkSpecificRepository();
        frameworkSpecificRepository.createUser(new FrameworkSpecificUser(1, "Some user"));

        //We are a framework-agnostic client
        var repositoryAdapter = new UserRepositoryAdapter(frameworkSpecificRepository);
        System.out.println(repositoryAdapter.ofId("1"));

        //hide complexity of existing API (class/interface, function, whatever)
        var hmacHasher = new HmacHasher();
        System.out.println(hmacHasher.toHash("some string", new byte[]{0, 1, 2,3 }));
    }

    static class FrameworkSpecificRepository {

        private final Map<Long, FrameworkSpecificUser> users = new HashMap<>();

        public void createUser(FrameworkSpecificUser user) {
            users.put(user.id, user);
        }

        public Optional<FrameworkSpecificUser> userOfId(long id) {
            return Optional.ofNullable(users.get(id));
        }
    }

    static class FrameworkSpecificUser {
        //@Id
        final long id;
        //Some another framework-specific annotation
        final String name;

        public FrameworkSpecificUser(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    interface UserRepository {
        Optional<User> ofId(String id);
    }

    record User(String id, String name){}

    private static class UserRepositoryAdapter implements UserRepository {

        private final FrameworkSpecificRepository repository;

        public UserRepositoryAdapter(FrameworkSpecificRepository repository) {
            this.repository = repository;
        }

        @Override
        public Optional<User> ofId(String id) {
            return repository.userOfId(Long.parseLong(id))
                    .map(u -> new User(String.valueOf(u.id), u.name));
        }
    }

    private static class HmacHasher {

        private final String hmacAlgorithm;

        public HmacHasher(String hmacAlgorithm) {
            this.hmacAlgorithm = hmacAlgorithm;
        }

        public HmacHasher() {
            this("HmacSHA256");
        }

        public String toHash(String string, byte[] key) {
            try {
                var mac = Mac.getInstance(hmacAlgorithm);
                mac.init(new SecretKeySpec(key, hmacAlgorithm));
                var macData = mac.doFinal(string.getBytes(StandardCharsets.UTF_8));
                return Base64.getUrlEncoder().withoutPadding().encodeToString(macData);
            } catch (Exception e) {
                throw new RuntimeException("Problem while hashing data with %s algorithm".formatted(hmacAlgorithm), e);
            }
        }
    }
}
