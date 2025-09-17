import org.xbill.DNS.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess
import kotlin.time.toJavaDuration
import kotlin.time.Duration.Companion.seconds

// Data class to hold the results of each test
data class DnsResult(
    val dnsServerName: String,
    val dnsServerIp: String,
    val resolvedIp: String?,
    val pingTimeMs: Double?
)

// The hostname you want to test
// A list of common DNS servers to test. Add or remove any you like.

private var TARGET_HOSTNAME: String? = ""

private val DNS_SERVERS_TO_TEST = mapOf(
    "Google 1" to "8.8.8.8",
    "Cloudflare 1" to "1.1.1.1",
    "OpenDNS" to "208.67.222.222",
    "Quad9" to "9.9.9.9",
    "Comodo Secure" to "8.26.56.26",
    "Shekan 1" to "178.22.122.100",
    "Shekan 2" to "185.51.200.2",
    "DNS 3" to "192.104.158.78",
    "DNS 4" to "208.67.220.220",
    "DNS 5" to "208.67.222.220",
    "DNS 6" to "208.67.220.222",
    "DNS 7" to "77.88.8.1",
    "DNS 8" to "77.88.8.8",
    "DNS 9" to "199.85.126.10",
    "DNS 10" to "199.85.127.10",
    "DNS 11" to "209.244.0.3",
    "DNS 12" to "209.244.0.4",
    "DNS 13" to "4.2.2.1",
    "DNS 14" to "4.2.2.2",
    "DNS 15" to "4.2.2.3",
    "DNS 16" to "4.2.2.4",
    "DNS 17" to "4.2.2.5",
    "DNS 18" to "4.2.2.6",
    "DNS 19" to "8.20.247.20",
    "DNS 20" to "216.146.35.35",
    "DNS 21" to "216.146.36.36",
    "DNS 22" to "198.153.192.1",
    "DNS 23" to "198.153.194.1",
    "DNS 24" to "156.154.70.22",
    "DNS 25" to "156.154.71.22",
    "DNS 26" to "64.6.64.6",
    "DNS 27" to "64.6.65.6",
    "DNS 28" to "205.171.3.65",
    "DNS 29" to "205.171.2.65",
    "DNS 30" to "204.97.212.10",
    "DNS 31" to "204.117.214.10",
    "DNS 32" to "195.46.39.39",
    "DNS 33" to "195.46.39.40",
    "DNS 34" to "84.200.69.80",
    "DNS 35" to "84.200.70.40",
    "DNS 36" to "199.2.252.10",
    "DNS 37" to "204.97.212.10",
    "DNS 38" to "204.69.234.1",
    "DNS 39" to "204.74.101.1",
    "DNS 40" to "212.23.8.1",
    "DNS 41" to "212.23.3.1",
    "DNS 42" to "195.92.195.94",
    "DNS 43" to "195.92.195.95",
    "DNS 44" to "74.82.42.42",
    "DNS 45" to "80.80.80.80",
    "DNS 46" to "80.80.81.81",
    "DNS 47" to "80.67.169.12",
    "DNS 48" to "80.67.169.40",
    "DNS 49" to "156.154.70.1",
    "DNS 50" to "156.154.71.1",
    "DNS 51" to "156.154.70.5",
    "DNS 52" to "156.154.71.5",
    "DNS 53" to "94.140.14.14",
    "DNS 54" to "94.140.15.15",
    "DNS 55" to "89.233.43.71",
    "DNS 56" to "91.239.100.100",
    "DNS 57" to "77.88.8.7",
    "DNS 58" to "77.88.8.3",
    "DNS 59" to "185.228.168.168",
    "DNS 60" to "185.228.169.168",
    "DNS 61" to "194.104.158.48",
    "DNS 62" to "172.29.0.100",
    "DNS 63" to "172.29.2.100",
    "DNS 64" to "10.202.10.202",
    "DNS 65" to "10.202.10.102",
    "DNS 66" to "185.55.226.26",
    "DNS 67" to "185.55.225.25",
    "DNS 68" to "10.202.10.10",
    "DNS 69" to "10.202.10.11",
    "DNS 70" to "37.27.41.228",
    "DNS 71" to "87.107.52.11",
    "DNS 72" to "87.107.52.13",
    "DNS 73" to "5.202.100.100",
    "DNS 74" to "5.202.100.101",
    "DNS 75" to "94.103.125.157",
    "DNS 76" to "94.103.125.158",
    "Google 2" to "8.8.4.4",
    "Cloudflare 2" to "1.0.0.1",
    "DNS 79" to "149.112.112.112",
    "DNS 80" to "149.112.112.10",
    "DNS 81" to "185.231.182.126",
    "DNS 82" to "37.152.182.112",
    "Electro 1" to "78.157.42.100",
    "Electro 2" to "78.157.42.101",
    "DNS 85" to "15.197.238.60",
    "DNS 86" to "3.33.242.199",
)

fun main() {
    print("Please enter your Link for Ping Test: ")
    TARGET_HOSTNAME = readLine()

    if (TARGET_HOSTNAME != "") {
        println("Starting Testing DNS for, $TARGET_HOSTNAME!")
    }
    else {
        println("   ")
        println("No Link entered.")
        // To stop the application immediately
        exitProcess(0) // The argument 0 indicates a successful termination

    }
    println("-".repeat(40))

    val results = mutableListOf<DnsResult>()

    DNS_SERVERS_TO_TEST.forEach { (name, dnsIp) ->


        // 1. Resolve the hostname to an IP using the specific DNS server
        val resolvedIp = resolveHostWithDns(TARGET_HOSTNAME, dnsIp)
        println("* Testing DNS: $name ($dnsIp)...")
        if (resolvedIp == null) {
            println("   $ DNS resolution failed.")
            results.add(DnsResult(name, dnsIp, null, null))
        } else {
            println("   Resolved to: $resolvedIp")

            // 2. Ping the resolved IP address
            val pingTime = pingIpAddress(resolvedIp)
            if (pingTime == null) {
                println("   $ Ping failed or timed out.")
            } else {
                println("   # Ping Time: ${"%.2f".format(pingTime)} ms")
            }
            results.add(DnsResult(name, dnsIp, resolvedIp, pingTime))
        }
    }

    printResults(results)
}

/**
 * Resolves a hostname to an IPv4 address using a specific DNS server.
 * @param hostname The hostname to resolve.
 * @param dnsServerIp The IP address of the DNS server to use.
 * @return The resolved IPv4 address as a String, or null if resolution fails.
 */
fun resolveHostWithDns(hostname: String?, dnsServerIp: String): String? {
    return try {
        val resolver = SimpleResolver(dnsServerIp).apply {
            // Set a timeout using a Java Duration
            timeout = 2.seconds.toJavaDuration()
        }

        val lookup = Lookup(hostname, Type.A)
        lookup.setResolver(resolver)

        val records = lookup.run()

        return if (lookup.result == Lookup.SUCCESSFUL && records?.isNotEmpty() == true) {
            (records[0] as ARecord).address.hostAddress
        } else {
            null
        }
    } catch (e: Exception) {
        // e.g., java.net.SocketTimeoutException
        System.err.println("Error during DNS lookup with $dnsServerIp: ${e.message}")
        return null
    }
}

/**
 * Pings an IP address and extracts the round-trip time.
 * @param ipAddress The IP address to ping.
 * @return The ping time in milliseconds as a Double, or null if it fails.
 */
fun pingIpAddress(ipAddress: String): Double? {
    try {
        val os = System.getProperty("os.name").toLowerCase()

        // Build the appropriate ping command based on the operating system
        val command = when {
            "win" in os -> listOf("ping", "-n", "1", "-w", "2000", ipAddress) // Windows: 1 count, 2000ms timeout
            else -> listOf("ping", "-c", "1", "-W", "2", ipAddress)         // Linux/macOS: 1 count, 2s timeout
        }

        val process = ProcessBuilder(command).start()
        val processOutput = process.inputStream.bufferedReader().readText()
        process.waitFor(3, TimeUnit.SECONDS) // Wait for max 3 seconds

        // Regex to find the time in the ping output (works for both Windows and Linux/macOS)
        val regex = Regex("""time[=<]([0-9.]+) ?ms""")
        val match = regex.find(processOutput)

        return match?.groups?.get(1)?.value?.toDoubleOrNull()

    } catch (e: Exception) {
        System.err.println("Error executing ping command for $ipAddress: ${e.message}")
        return null
    }
}

/**
 * Prints the final results in a formatted table.
 */
fun printResults(results: List<DnsResult>) {
    println("\n" + "=".repeat(40))
    println("# Final Results (sorted by best ping)")
    println("=".repeat(40))

    // Sort results by ping time, putting failures at the end
    val sortedResults = results.sortedWith(
        compareBy(nullsLast()) { it.pingTimeMs }
    )

    println("%-15s %-15s %-18s %-10s".format("DNS Server", "DNS IP", "Resolved IP", "Ping (ms)"))
    println("-".repeat(65))

    sortedResults.forEach { result ->
        val pingDisplay = result.pingTimeMs?.let { "%.2f".format(it) } ?: "FAILED"
        val resolvedIpDisplay = result.resolvedIp ?: "N/A"
        println("%-15s %-15s %-18s %-10s".format(result.dnsServerName, result.dnsServerIp, resolvedIpDisplay, pingDisplay))
    }

    val best = sortedResults.firstOrNull { it.pingTimeMs != null }
    if (best != null) {
        println("\n" + "=".repeat(40))
        println("# Best DNS Server: ${best.dnsServerName} with ${"%.2f".format(best.pingTimeMs)} ms ping.")
        println("=".repeat(40))
    } else {
        println("\n# No servers could successfully resolve and ping the host.")
    }
}
