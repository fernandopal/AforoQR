package es.fernandopal.aforoqr.api.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import es.fernandopal.aforoqr.api.Constants;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class QRCodeGenerator {
    private final Long entityId;
    int size = 250;

    public QRCodeGenerator(Long entityId) {
        this.entityId = entityId;
    }

    public Path generate() throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(String.valueOf(entityId), BarcodeFormat.QR_CODE, size, size); // Queremos que el QR sea cuadrado así que alto y ancho = size

//            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//            MatrixToImageConfig con = new MatrixToImageConfig( 0xFF000002 , 0xFFFFC041 ) ;
//
//            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream,con);
//            return pngOutputStream.toByteArray();

        final Path path = FileSystems.getDefault().getPath(Constants.QR_STORAGE_PATH + "/" + entityId + ".png");
        Files.createDirectories(path.getParent());
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return path;
    }
}