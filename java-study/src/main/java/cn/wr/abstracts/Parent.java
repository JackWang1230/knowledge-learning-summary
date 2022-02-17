package cn.wr.abstracts;

public interface Parent<T> extends GrandFather<T> {
    @Override
    long print(T dd,long aa);
}
