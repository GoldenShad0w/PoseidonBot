plugins {
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application.mainClass = "me.goldenshadow.poseidon.Poseidon"
group = "me.goldenshadow"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

val jdaVersion = "5.0.0-beta.13"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.dv8tion:JDA:$jdaVersion")
    implementation("org.yaml:snakeyaml:2.2")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("ch.qos.logback:logback-classic:1.2.9")
    //implementation("com.github.Bedo9041:jWynn:0.8.3")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("commons-io:commons-io:2.8.0")


}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true

    // Set this to the version of java you want to use,
    // the minimum required for JDA is 1.8
    sourceCompatibility = "17"
}