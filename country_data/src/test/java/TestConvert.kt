import com.blongho.country_data.World
import com.google.gson.Gson
import org.junit.Test
import java.io.File

class TestConvert {

    @Test
    fun test(){
        val path = "src/main/res"
        val imagesPath = "$path/drawable"

        val file = File(imagesPath)
        val icons = file.listFiles() ?: emptyArray()
        icons.forEach { iconFile ->
            iconFile.renameTo(File(iconFile.parent, iconFile.name.replace("ic_", "").replace("_svg", "")))
            if (!iconFile.name.startsWith("lang_")) {
                iconFile.renameTo(File(iconFile.parent, iconFile.name.replace(iconFile.name, "lang_${iconFile.name}")))
            }
        }
        val languages = arrayListOf<Lang>()
        var id = 0
        icons.forEach { iconFile ->
            val name = iconFile.nameWithoutExtension
            languages.add(Lang(name, id.toString()))
            id++
        }
        languages.add(Lang("xxx", "999"))
        val json = Gson().toJson(languages)
        println(json)
    }

    class Lang(val alpha3: String, val id: String)
}