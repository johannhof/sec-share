package file_services;

public interface CryptoHelper {
    public abstract byte[] transform(byte[] in, boolean last);
}
