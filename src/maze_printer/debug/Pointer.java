package maze_printer.debug;

/*
 * For use with immutable classes
 * Mutable classes are fine by themselves
 */
public class Pointer<T> {
	public Pointer() {}
	public Pointer(T value) { this.value = value; }
	public T value;
}
