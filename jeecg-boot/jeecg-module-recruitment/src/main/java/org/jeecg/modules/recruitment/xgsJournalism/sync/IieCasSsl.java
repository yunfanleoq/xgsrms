package org.jeecg.modules.recruitment.xgsJournalism.sync;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 信工所官网证书链由 CFCA EV ROOT 签发。JDK 默认信任库没有这张根，
 * macOS 系统信任库有。这里只为同步客户端补上这张公开根证书，并继续校验主机名。
 */
final class IieCasSsl {

    private static volatile SSLContext context;

    private IieCasSsl() {
    }

    static SSLContext context() {
        SSLContext existing = context;
        if (existing == null) {
            synchronized (IieCasSsl.class) {
                if (context == null) {
                    context = build();
                }
                existing = context;
            }
        }
        return existing;
    }

    private static SSLContext build() {
        try (InputStream in = IieCasSsl.class.getClassLoader().getResourceAsStream("certs/cfca-ev-root.pem")) {
            if (in == null) {
                throw new IllegalStateException("缺少证书资源 certs/cfca-ev-root.pem");
            }
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cfcaRoot = (X509Certificate) certificateFactory.generateCertificate(in);

            TrustManagerFactory defaults = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            defaults.init((KeyStore) null);

            KeyStore pinnedStore = KeyStore.getInstance(KeyStore.getDefaultType());
            pinnedStore.load(null, null);
            pinnedStore.setCertificateEntry("cfca-ev-root", cfcaRoot);
            TrustManagerFactory pinned = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            pinned.init(pinnedStore);

            X509TrustManager defaultManager = findX509(defaults.getTrustManagers());
            X509TrustManager pinnedManager = findX509(pinned.getTrustManagers());
            X509TrustManager combined = new CompositeTrust(defaultManager, pinnedManager);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{combined}, null);
            return context;
        } catch (Exception ex) {
            throw new IllegalStateException("初始化官网 HTTPS 信任失败", ex);
        }
    }

    private static X509TrustManager findX509(TrustManager[] managers) {
        for (TrustManager manager : managers) {
            if (manager instanceof X509TrustManager) {
                return (X509TrustManager) manager;
            }
        }
        throw new IllegalStateException("没有 X509TrustManager");
    }

    private static final class CompositeTrust implements X509TrustManager {
        private final X509TrustManager primary;
        private final X509TrustManager extra;

        private CompositeTrust(X509TrustManager primary, X509TrustManager extra) {
            this.primary = primary;
            this.extra = extra;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            primary.checkClientTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                primary.checkServerTrusted(chain, authType);
            } catch (CertificateException primaryError) {
                try {
                    extra.checkServerTrusted(chain, authType);
                } catch (CertificateException extraError) {
                    primaryError.addSuppressed(extraError);
                    throw primaryError;
                }
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            List<X509Certificate> issuers = new ArrayList<>();
            issuers.addAll(Arrays.asList(primary.getAcceptedIssuers()));
            issuers.addAll(Arrays.asList(extra.getAcceptedIssuers()));
            return issuers.toArray(new X509Certificate[0]);
        }
    }
}
