const express = require("express");
const { exec } = require("child_process");

const app = express();

app.get("/cpu", (req, res) => {
  exec("sh /app/scripts/cpu_stats.sh", (err, stdout, stderr) => {
    console.log("STDOUT:", stdout);
    console.log("STDERR:", stderr);
    console.log("ERROR:", err);

    if (err) return res.status(500).send("Error");
    res.json({ cpu: stdout.trim() });
  });
});
app.get("/ram", (req, res) => {
  exec("sh /app/scripts/ram_stats.sh", (err, stdout) => {
    if (err) return res.status(500).send("Error");
    res.json({ used: stdout.trim() });
  });
});

app.get("/disk", (req, res) => {
  exec("sh /app/scripts/disk_stats.sh", (err, stdout) => {
    if (err) return res.status(500).send("Error");
    res.json({ used: stdout.trim() });
  });
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
