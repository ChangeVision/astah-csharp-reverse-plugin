using System;

/// <summary>
///    クラスAaa
/// </summary>
public class Aaa
{
    /// <summary>
    ///    文字列aaa
    /// </summary>
    public String aaa;

    /// <summary>
    ///    メソッドaaa()
    /// </summary>
    public void aaa() { }
}

/// <summary>
///    <newpara>
///       クラスBbb
///       複数行コメント
///    </newpara>
///    summary
/// </summary>
/// 
/// <remarks>
///    <newpara>
///       クラスBbb
///       複数行コメント
///    </newpara>
///    remarks
/// </remarks>
public class Bbb
{
    /// <summary>
    ///    文字列bbb
    ///    複数行コメント
    ///    summary
    /// </summary>
    /// 
    /// <remarks>
    ///    <newpara>
    ///       クラスBbb
    ///       複数行コメント
    ///    </newpara>
    ///    remarks
    /// </remarks>
    public String bbb;

    /// <summary>
    ///    メソッドbbb()
    ///    複数行コメント
    /// </summary>
    /// <remarks>
    ///    remarks
    ///    解説
    /// </remarks>
    /// <param name="i">
    ///    数値
    /// </param>
    /// <returns>
    ///    void
    /// </returns>
    public void bbb(int i) { }
}
