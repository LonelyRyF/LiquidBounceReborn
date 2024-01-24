package cn.liying.fonts.api;

/**
 * @author Artyom Popov
 * @since June 30, 2020
 */
@SuppressWarnings("SpellCheckingInspection")
public enum FontType {

	//DM("diramight.ttf"),
	FIXEDSYS("tahoma.ttf"),
	ICONFONT("stylesicons.ttf"),
//	FluxICONFONT("flux.ttf"),
	Check("check.ttf"),
	SF("SF.ttf"),
	SFBOLD("SFBOLD.ttf"),
	JelloMedium("jellomedium.ttf"),
	Tahoma("tahoma.ttf"),
	JelloLight("jellolight.ttf"),
	csgoicon("icomoon.ttf");
	//CHINESE("black.ttf"),
	//Tahoma("Tahoma.ttf"),
	//TahomaBold("Tahoma-Bold.ttf"),
	//SFTHIN("SFREGULAR.ttf"),
	//OXIDE("oxide.ttf"),
//	Museo("MuseoSans_900.ttf"),
	//Eaves("Eaves.ttf");

	private final String fileName;

	FontType(String fileName) {
		this.fileName = fileName;
	}

	public String fileName() { return fileName; }
}
