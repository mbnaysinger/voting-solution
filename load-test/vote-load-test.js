import http from 'k6/http';
import { check } from 'k6';
import { randomSeed } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

randomSeed(Date.now());

export const options = {
  scenarios: {
    voting: {
      executor: 'constant-arrival-rate',
      rate: Number(1),    //1 voto
      timeUnit: '2s',           //a cada 2 seg
      duration: String('2m'),
      preAllocatedVUs: 10,
      maxVUs: 50,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.05'],
  },
};

const BASE_URL = 'http://host.docker.internal:8080/api/v1/agenda';

function pad(v) { return String(v).padStart(2, '0'); }
function formatLocalDateTimePlusSeconds(seconds) {
  const d = new Date(Date.now() + seconds * 1000);
  const yyyy = d.getFullYear();
  const MM = pad(d.getMonth() + 1);
  const dd = pad(d.getDate());
  const HH = pad(d.getHours() - 3);
  const mm = pad(d.getMinutes());
  const ss = pad(d.getSeconds());
  return `${yyyy}-${MM}-${dd}T${HH}:${mm}:${ss}`;
}

// CPF válido com DV
function generateValidCPF() {
  const n = [];
  for (let i = 0; i < 9; i++) n.push(Math.floor(Math.random() * 10));
  let sum = 0;
  for (let i = 0, w = 10; i < 9; i++, w--) sum += n[i] * w;
  let dv1 = sum % 11;
  dv1 = dv1 < 2 ? 0 : 11 - dv1;
  sum = 0;
  for (let i = 0, w = 11; i < 9; i++, w--) sum += n[i] * w;
  sum += dv1 * 2;
  let dv2 = sum % 11;
  dv2 = dv2 < 2 ? 0 : 11 - dv2;
  return n.join('') + String(dv1) + String(dv2);
}

function randomVoteType() {
  return Math.random() < 0.5 ? 'YES' : 'NO';
}

export function setup() {
  const payload = {
    title: 'Teste de Carga k6',
    description: 'Cenário de carga de votos',
    createdBy: 'k6',
    sessionStartTime: formatLocalDateTimePlusSeconds(2),
    sessionDurationMinutes: 3,
  };
  const res = http.post(`${BASE_URL}`, JSON.stringify(payload), {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'create agenda: 201/200': (r) => r.status === 201 || r.status === 200,
  });

  const body = res.json();
  const sessions = body?.sessions || [];
  if (!sessions.length || !sessions[0].sessionId) {
    throw new Error('Não foi possível obter sessionId na resposta de criação de agenda');
  }
  const sessionId = sessions[0].sessionId;
  return { sessionId };
}

export default function (data) {
  const userId = `user_${Math.random().toString(36).slice(2, 10)}`;
  const cpf = generateValidCPF();
  const voteType = randomVoteType();

  const res = http.post(
    `${BASE_URL}/session/${data.sessionId}/vote`,
    JSON.stringify({ userId, cpf, voteType }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(res, {
    'vote ok (201 ou 404)': (r) => r.status === 201 || r.status === 404,
  });
}

export function handleSummary(data) {
  return {
    '/load-test/summary.html': htmlReport(data),
    '/load-test/summary.json': JSON.stringify(data, null, 2),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}
