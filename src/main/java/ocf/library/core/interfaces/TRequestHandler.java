package ocf.library.core.interfaces;

public interface TRequestHandler <T,U>{
	public U handle(T request);
}
