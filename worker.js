export default {
  async fetch(request, env, ctx) {
    // Cloudflare Worker to serve cashfive.csv to the Texas Cash Five Quick Hits Android app
    // Store your raw CSV in Cloudflare KV or fetch from GitHub raw URL, then return it here.
    const csvUrl = "https://raw.githubusercontent.com/fjlove96/TexasCashFiveQuickHits/main/cashfive.csv";

    try {
      const response = await fetch(csvUrl);
      const csvText = await response.text();
      return new Response(csvText, {
        headers: {
          "Content-Type": "text/csv; charset=utf-8",
          "Access-Control-Allow-Origin": "*"
        }
      });
    } catch (e) {
      return new Response("Error fetching CSV", { status: 500 });
    }
  }
};
