package com.xiaofei.certificatetest.service.impl;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: xiaofei
 * Date: 2023-04-08, 1:29
 * Description:
 */
public class JNDIDirectoryImpl {
    public static void main(String[] args) {
        String ldapURL = "ldap://localhost:389";
        String ldapUsername = "cn=admin,dc=mycompany,dc=com";
        String ldapPassword = "password";
        String searchBase = "ou=users,dc=mycompany,dc=com";

        Hashtable<String, String> env = new Hashtable<>(); 
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL,ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION,"simple");
        env.put(Context.SECURITY_PRINCIPAL,ldapURL);
        env.put(Context.SECURITY_CREDENTIALS,ldapPassword);
        
        try {
            DirContext context = new InitialDirContext(env);

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // Perform the LDAP search
            NamingEnumeration<SearchResult> results = context.search(searchBase, "(objectClass=person)", searchControls);

            // Iterate through the search results
            while (results.hasMore()) {
                SearchResult searchResult = results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute commonName = attributes.get("cn");
                String cn = (String) commonName.get();
                System.out.println("Found user: " + cn);
            }

            // Close the context
            context.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
