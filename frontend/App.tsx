import React, { useState } from 'react';
import { Send, Bot, User } from 'lucide-react';
import axios from 'axios';

interface Message {
    text: string;
    isBot: boolean;
}

function App() {
    const [messages, setMessages] = useState<Message[]>([
        { text: "Welcome to Concierge. How can I help with your booking?", isBot: true }
    ]);
    const [input, setInput] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleSend = async () => {
        if (!input.trim() || isLoading) return;

        const userMsg = { text: input, isBot: false };
        setMessages(prev => [...prev, userMsg]);
        setInput('');
        setIsLoading(true);

        try {
            // This hits the Vite Proxy we set up in vite.config.ts
            const response = await axios.post('/api/chat', { message: input });
            setMessages(prev => [...prev, { text: response.data.reply, isBot: true }]);
        } catch (error) {
            setMessages(prev => [...prev, { text: "Error connecting to backend. Is the JAR running?", isBot: true }]);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex flex-col h-screen bg-slate-900 text-white font-sans">
            {/* Header */}
            <div className="p-6 border-b border-slate-700 bg-slate-800 flex items-center gap-3">
                <div className="bg-blue-600 p-2 rounded-lg"><Bot size={24} /></div>
                <h1 className="text-xl font-bold tracking-tight">CONCIERGE</h1>
            </div>

            {/* Chat Area */}
            <div className="flex-1 overflow-y-auto p-6 space-y-4">
                {messages.map((m, i) => (
                    <div key={i} className={`flex ${m.isBot ? 'justify-start' : 'justify-end'}`}>
                        <div className={`max-w-[80%] p-4 rounded-2xl shadow-sm ${m.isBot ? 'bg-slate-800 text-slate-100 border border-slate-700' : 'bg-blue-600 text-white'
                            }`}>
                            <p className="text-sm leading-relaxed">{m.text}</p>
                        </div>
                    </div>
                ))}
                {isLoading && <div className="text-slate-500 text-xs animate-pulse">Bot is thinking...</div>}
            </div>

            {/* Input Area */}
            <div className="p-6 bg-slate-800 border-t border-slate-700">
                <div className="flex gap-3 max-w-4xl mx-auto">
                    <input
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                        placeholder="Ask about a booking..."
                        className="flex-1 bg-slate-900 border border-slate-700 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                    />
                    <button
                        onClick={handleSend}
                        className="bg-blue-600 hover:bg-blue-500 p-3 rounded-xl transition-colors disabled:opacity-50"
                        disabled={isLoading}
                    >
                        <Send size={20} />
                    </button>
                </div>
            </div>
        </div>
    );
}

export default App;