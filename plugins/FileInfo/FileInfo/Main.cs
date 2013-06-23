using System;
using System.Diagnostics;
using System.IO;

namespace FileInfo
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			FileVersionInfo v = FileVersionInfo.GetVersionInfo("C:\\Users\\kofspades\\Downloads\\Dwolla.Ach.dll");

			var json = string.Format (@"
{{
""comments"":""{0}"",
""companyName"":""{1}"",
""fileBuildPart"":""{2}"",
""fileDescription"":""{3}"",
""fileMajorPart"":""{4}"",
""fileMinorPart"":""{5}"",
""filename"":""{6}"",
""filePrivatePart"":""{7}"",
""fileVersion"":""{8}"",
""internalName"":""{9}"",
""isDebug"":""{10}"",
""isPatched"":""{11}"",
""isPreRelease"":""{12}"",
""isPrivateBuild"":""{13}"",
""isSpecialBuild"":""{14}"",
""language"":""{15}"",
""legalCopyright"":""{16}"",
""legalTrademarks"":""{17}"",
""originalFileName"":""{18}"",
""privateBuild"":""{19}"",
""productBuildPart"":""{20}"",
""productMajorPart"":""{21}"",
""productMinorPart"":""{22}"",
""productName"":""{23}"",
""productPrivatePart"":""{24}"",
""productVersion"":""{25}"",
""specialBuild"":""{26}""
}}", v.Comments, v.CompanyName, v.FileBuildPart, v.FileDescription, v.FileMajorPart, v.FileMinorPart, v.FileName, v.FilePrivatePart, v.FileVersion, v.InternalName
			                          , v.IsDebug, v.IsPatched, v.IsPreRelease, v.IsPrivateBuild, v.IsSpecialBuild, v.Language, v.LegalCopyright, v.LegalTrademarks,
			                          v.OriginalFilename, v.PrivateBuild, v.ProductBuildPart, v.ProductMajorPart, v.ProductMinorPart, v.ProductName, v.ProductPrivatePart,
			                          v.ProductVersion, v.SpecialBuild);


			// Print the file name and version number.
			Console.WriteLine(json);
		}
	}

}
