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
///    クラスAです。
/// </summary>
/// <remarks>
///    クラスAの詳しい説明。
/// </remarks>
public class ClassA
{

	/// <summary>
	///    文字列a
	/// </summary>
	/// <remarks>
	///    文字列aの詳しい説明
	/// </remarks>
	private String a;


	/// <summary>
	///    文字列を連結して取得します。
	/// </summary>
	/// <remarks>
	///    パラメータで渡された文字列を連結し、返します。
	/// </remarks>
	/// <param name="str1">
	///    1つ目の文字列
	/// </param>
	/// <param name="str2">
	///    2つ目の文字列
	/// </param>
	/// <returns>
	///    連結後の文字列
	/// </returns>
	public String getJoinString(String str1, String str2) {
		return str1 + str2;
	 }
	

}

