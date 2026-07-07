import { useState, useEffect } from 'react'
import Navbar from '../../../shared/ui/Navbar'
import { Trophy, Medal, Star } from 'lucide-react'
import apiClient from '../../../shared/lib/apiClient'

function LeaderboardPage() {
  const [topCitizens, setTopCitizens] = useState([])
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    const controller = new AbortController();
    async function fetchLeaderboard() {
      try {
        console.log("Iniciando carga de ranking...");
        const { data } = await apiClient.get('/users/leaderboard', {
          signal: controller.signal
        })
        console.log("Respuesta del ranking:", data);
        if (data && data.success) {
          setTopCitizens(Array.isArray(data.data) ? data.data : [])
        } else {
          console.error("Ranking no tuvo exito:", data);
        }
      } catch (err) {
        if (err.name !== 'CanceledError') {
          console.error("Error fetching leaderboard", err)
        }
      } finally {
        setCargando(false)
        console.log("Carga de ranking finalizada.");
      }
    }
    fetchLeaderboard()
    return () => controller.abort();
  }, [])

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <main className="container mx-auto px-4 py-12 max-w-3xl animate-fade-in mt-16">
        <div className="glass-panel p-8 rounded-3xl relative overflow-hidden">
          <div className="absolute top-0 right-0 w-64 h-64 bg-accent-600/10 rounded-full blur-3xl"></div>
          
          <div className="flex items-center gap-4 mb-8 relative z-10">
            <div className="bg-accent-500/20 p-4 rounded-2xl">
              <Trophy className="text-accent-400" size={32} />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-white">Ranking Cívico</h1>
              <p className="text-gray-400 mt-1">Los ciudadanos más activos de la municipalidad</p>
            </div>
          </div>

          <div className="space-y-4 relative z-10">
            {cargando ? (
              <div className="text-center text-gray-400 py-8">Cargando ranking...</div>
            ) : topCitizens.length === 0 ? (
              <div className="text-center text-gray-400 py-8">No hay ciudadanos en el ranking todavía.</div>
            ) : (
              topCitizens.map((citizen, index) => (
                <div 
                  key={citizen.id} 
                  className={`flex items-center gap-4 p-4 rounded-2xl border transition-all hover:-translate-y-1 ${
                    index === 0 ? 'bg-yellow-500/10 border-yellow-500/30 shadow-[0_0_15px_rgba(234,179,8,0.2)]' :
                    index === 1 ? 'bg-gray-300/10 border-gray-300/30' :
                    index === 2 ? 'bg-amber-700/10 border-amber-700/30' :
                    'bg-surface/50 border-white/5'
                  }`}
                >
                  <div className="w-12 text-center font-bold text-xl">
                    {index === 0 ? <Medal className="mx-auto text-yellow-500" size={28} /> :
                     index === 1 ? <Medal className="mx-auto text-gray-300" size={28} /> :
                     index === 2 ? <Medal className="mx-auto text-amber-700" size={28} /> :
                     <span className="text-gray-500">#{index + 1}</span>}
                  </div>
                  
                  <div className="flex-1">
                    <h3 className="font-bold text-white text-lg flex items-center gap-2">
                      {citizen.firstName} {citizen.lastName}
                    </h3>
                  </div>

                  <div className="flex flex-col items-end">
                    <div className="flex items-center gap-1 bg-accent-500/20 px-3 py-1 rounded-full border border-accent-500/30">
                      <Star className="text-accent-400" size={16} fill="currentColor" />
                      <span className="font-bold text-accent-400">{citizen.civicScore || 0} pts</span>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </main>
    </div>
  )
}

export default LeaderboardPage
