public class Aaa
{
    static Bbb log = new Bbb(new Fuga(new Hoge()));
}

public class Bbb
{
    public Bbb(Fuga fuga) { }
}

public class Hoge { }

public class Fuga
{
    public Fuga(Hoge hoge) { }

}