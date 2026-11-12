export default {
  async fetch(request, env, ctx) {
    try {
      // Reads cashfive.csv directly from the bound Cloudflare R2 bucket
      const object = await env.CASHFIVE_BUCKET.get("cashfive.csv");
      if (object === null) {
        return new Response("CSV not found in R2 bucket", { status: 404 });
      }
      const csvText = await object.text();
      return new Response(csvText, {
        headers: {
          "Content-Type": "text/csv; charset=utf-8",
          "Access-Control-Allow-Origin": "*"
        }
      });
    } catch (e) {
      return new Response("Error fetching CSV from R2: " + e.message, { status: 500 });
    }
  }
};
