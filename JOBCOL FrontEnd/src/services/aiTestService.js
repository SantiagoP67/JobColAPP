const OPENAI_API_KEY = "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";

function getDifficulty(questionNumber, previousQA) {
  if (questionNumber === 1) return "basico";
  const correctCount = previousQA.filter(q => q.wasCorrect).length;
  if (correctCount === previousQA.length) {
    return questionNumber === 2 ? "intermedio" : "avanzado";
  }
  return questionNumber === 2 ? "basico-intermedio" : "intermedio";
}

export const generateQuestion = async (category, questionNumber, previousQA = []) => {
  const difficulty = getDifficulty(questionNumber, previousQA);

  const previousContext = previousQA.length > 0
    ? `\nPreguntas anteriores y respuestas:\n${previousQA.map((qa, i) => `${i + 1}. Pregunta: "${qa.question}" - Respuesta: "${qa.answer}" - ${qa.wasCorrect ? "CORRECTA" : "INCORRECTA"}`).join('\n')}`
    : '';

  const shouldBeOpen = questionNumber === 2 || (questionNumber === 3 && previousQA.filter(q => q.wasCorrect).length >= 2);

  const prompt = `Eres un evaluador experto en la categoria "${category}" para trabajo informal en Colombia.

Genera UNA sola pregunta de nivel ${difficulty} (pregunta ${questionNumber} de 3).
${previousContext}

REGLAS IMPORTANTES:
- La pregunta debe tener DIFICULTAD real y evaluar conocimiento práctico genuino del oficio.
- Sin embargo, la respuesta correcta debe ser CORTA y CONCISA (no más de 1-2 oraciones si es abierta, o una sola opción si es cerrada).
- NO hagas preguntas que requieran respuestas largas, explicaciones extensas o párrafos largos.
- Enfócate en situaciones prácticas reales del oficio, herramientas, seguridad o procedimientos específicos.
- Evita preguntas genéricas o de sentido común.

${shouldBeOpen
      ? 'La pregunta debe ser ABIERTA (el candidato escribe su respuesta). Debe poder responderse de forma breve y directa (1-2 oraciones máximo). Evalúa conocimiento práctico real.'
      : 'La pregunta debe ser de SELECCION MULTIPLE con exactamente 4 opciones. Solo una es correcta. Las opciones incorrectas deben ser plausibles.'}

Responde SOLO con JSON valido en este formato exacto (sin markdown, sin backticks):
${shouldBeOpen
      ? '{"question": "texto de la pregunta", "type": "open", "rubric": "criterios para evaluar la respuesta correcta"}'
      : '{"question": "texto de la pregunta", "type": "closed", "options": ["opcion A", "opcion B", "opcion C", "opcion D"], "correctIndex": 0}'}`;

  try {
    const response = await fetch("https://api.openai.com/v1/chat/completions", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${OPENAI_API_KEY}`
      },
      body: JSON.stringify({
        model: "gpt-4o-mini",
        messages: [{ role: "user", content: prompt }],
        temperature: 0.7,
        max_tokens: 500
      })
    });

    const data = await response.json();
    const content = data.choices?.[0]?.message?.content?.trim();
    const cleaned = content.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim();
    return JSON.parse(cleaned);
  } catch (e) {
    console.error("Error generating question:", e);
    return {
      question: `Cual es un aspecto fundamental en ${category}?`,
      type: "closed",
      options: ["Seguridad", "Velocidad", "Precio bajo", "Ninguno"],
      correctIndex: 0
    };
  }
};

export const evaluateOpenAnswer = async (question, answer, rubric, category) => {
  const prompt = `Eres evaluador experto en "${category}".

Pregunta: "${question}"
Respuesta del candidato: "${answer}"
Criterios de evaluacion: ${rubric}

Evalua la respuesta del candidato. Responde SOLO con JSON valido (sin markdown, sin backticks):
{"score": 75, "feedback": "breve retroalimentacion en espanol"}`;

  try {
    const response = await fetch("https://api.openai.com/v1/chat/completions", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${OPENAI_API_KEY}`
      },
      body: JSON.stringify({
        model: "gpt-4o-mini",
        messages: [{ role: "user", content: prompt }],
        temperature: 0.3,
        max_tokens: 200
      })
    });

    const data = await response.json();
    const content = data.choices?.[0]?.message?.content?.trim();
    const cleaned = content.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim();
    return JSON.parse(cleaned);
  } catch (e) {
    return { score: 50, feedback: "No se pudo evaluar la respuesta automaticamente." };
  }
};
