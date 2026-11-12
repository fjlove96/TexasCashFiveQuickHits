export default {
  async fetch(request, env, ctx) {
    // POST endpoint to update CSV from GitHub Action securely
    if (request.method === "POST") {
      const authHeader = request.headers.get("Authorization");
      if (!env.WORKER_SECRET || authHeader !== `Bearer ${env.WORKER_SECRET}`) {
        return new Response("Unauthorized", { status: 401 });
      }
      const csvText = await request.text();
      await env.CASHFIVE_BUCKET.put("cashfive.csv", csvText);
      return new Response("CSV successfully updated in R2!", { status: 200 });
    }

    // GET endpoint to serve CSV to the Android app
    try {
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
