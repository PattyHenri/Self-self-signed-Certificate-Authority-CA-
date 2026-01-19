package com.patty.takehome.server.tls;

import jakarta.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;
import java.util.Optional;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.springframework.stereotype.Component;

@Component
public class ClientCertExtractor {

  /**
   * Tomcat exposes the client cert chain at request attribute "jakarta.servlet.request.X509Certificate"
   * (older servlet API used "javax.servlet.request.X509Certificate").
   */
  public Optional<X509Certificate> getClientCertificate(HttpServletRequest request) {
    Object attr = request.getAttribute("jakarta.servlet.request.X509Certificate");
    if (attr == null) {
      attr = request.getAttribute("javax.servlet.request.X509Certificate");
    }
    if (attr instanceof X509Certificate[] chain && chain.length > 0) {
      return Optional.of(chain[0]);
    }
    return Optional.empty();
  }

  public Optional<String> extractCn(X509Certificate cert) {
    try {
      String dn = cert.getSubjectX500Principal().getName(); // e.g., CN=alice@example.com, O=...
      LdapName ldapName = new LdapName(dn);
      for (Rdn rdn : ldapName.getRdns()) {
        if ("CN".equalsIgnoreCase(rdn.getType())) {
          Object val = rdn.getValue();
          return Optional.ofNullable(val).map(Object::toString);
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
