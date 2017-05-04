package dsign.example.com.dsigntest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CertificateInfo;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.itextpdf.text.pdf.security.PrivateKeySignature;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class SignPDF {

    public static void sign(String src, String dest,
                     Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider,
                     MakeSignature.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException, DocumentException {

        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);

        ExternalDigest digest = new BouncyCastleDigest();
        ExternalSignature signature =
                new PrivateKeySignature(pk, digestAlgorithm, provider);
        MakeSignature.signDetached(appearance, digest, signature, chain,
                null, null, null, 0, subfilter);
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String configureCertificate(Activity a, String source)
            throws IOException, GeneralSecurityException {

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        String path = a.getSharedPreferences(
                "DSIGN", Context.MODE_PRIVATE).getString("CERT_PATH", null);
        InputStream inputStreamFromDownload = new FileInputStream(path);



        String pkpass = a.getSharedPreferences(
                "DSIGN", Context.MODE_PRIVATE).getString("PK_PASS", null);
        char[] password = pkpass.toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12", provider.getName());
        ks.load(inputStreamFromDownload, password);

        String alias = (String) ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, password);
        Certificate[] chain = ks.getCertificateChain(alias);
        String filename = source.substring(source.lastIndexOf("/")+1);
        filename = filename.replace(".pdf", "") + "_signed.pdf";


        String destination = source.replace(source.substring(source.lastIndexOf("/")+1), "");

        try {

            sign(source, destination + filename, chain, pk, DigestAlgorithms.SHA256, provider.getName(),
                    MakeSignature.CryptoStandard.CMS, "Digital Signature by DSign App", "IITU, Almaty");


        } catch (DocumentException e) {

            e.printStackTrace();
        }
        return destination + filename;
    }

    public static String showSigner(Activity a, String path) throws IOException, GeneralSecurityException {

//        int permission = ActivityCompat.checkSelfPermission(a, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(
//                    a,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }

        PdfReader reader = new PdfReader(path);
        AcroFields fields = reader.getAcroFields();
        ArrayList<String> names = fields.getSignatureNames();
        for (String name : names) {
            PdfPKCS7 pkcs7 = fields.verifySignature(name);
            X509Certificate cert = pkcs7.getSigningCertificate();

            Snackbar mySnackbar = Snackbar.make(a.findViewById(R.id.activity_verify),
                    "Signed by: " + CertificateInfo.getSubjectFields(cert).getField("CN"), Snackbar.LENGTH_SHORT);
            mySnackbar.show();
            System.out.println(CertificateInfo.getSubjectFields(cert).getField("CN")); ;
        }
        return "";



    }



    




}



